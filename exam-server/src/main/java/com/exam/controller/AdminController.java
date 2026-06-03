package com.exam.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.exam.common.Result;
import com.exam.entity.User;
import com.exam.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final JdbcTemplate jdbcTemplate;
    private final UserMapper userMapper;

    /**
     * 修复用户 ID 断层，使 ID 连续
     */
    @PostMapping("/fix-ids")
    public Result<?> fixUserIds() {
        // 获取所有用户按ID排序
        List<User> users = userMapper.selectList(
                new LambdaQueryWrapper<User>().orderByAsc(User::getId));

        // 第一步：把所有ID临时改为负数，避免冲突
        long offset = 100000;
        for (int i = 0; i < users.size(); i++) {
            jdbcTemplate.update("UPDATE sys_user SET id = ? WHERE id = ?",
                    -(offset + i), users.get(i).getId());
        }

        // 第二步：按正确顺序重新分配连续ID
        for (int i = 0; i < users.size(); i++) {
            jdbcTemplate.update("UPDATE sys_user SET id = ? WHERE id = ?",
                    i + 1, -(offset + i));
        }

        // 重置自增序列
        int nextId = users.size() + 1;
        jdbcTemplate.update("ALTER TABLE sys_user ALTER COLUMN id RESTART WITH ?", nextId);

        return Result.ok("ID 已修复为连续编号 1-" + users.size() + "，下一个新增用户ID: " + nextId);
    }

    @GetMapping("/max-id")
    public Result<?> getMaxId() {
        Long maxId = jdbcTemplate.queryForObject(
                "SELECT MAX(id) FROM sys_user", Long.class);
        return Result.ok(maxId);
    }
}
