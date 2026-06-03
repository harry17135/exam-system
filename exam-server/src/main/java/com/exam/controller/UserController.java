package com.exam.controller;

import com.exam.common.Result;
import com.exam.dto.ChangePasswordDTO;
import com.exam.entity.User;
import com.exam.service.UserService;
import com.exam.utils.UserContextUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
    public Result<?> list(@RequestParam(defaultValue = "1") Integer pageNum,
                          @RequestParam(defaultValue = "10") Integer pageSize,
                          @RequestParam(required = false) String keyword,
                          @RequestParam(required = false) String role) {
        return Result.ok(userService.listPage(pageNum, pageSize, keyword, role));
    }

    @GetMapping("/profile")
    public Result<?> profile() {
        return Result.ok(userService.getById(UserContextUtil.getCurrentUserId()));
    }

    @PutMapping("/profile")
    public Result<?> updateProfile(@RequestBody User user) {
        user.setId(UserContextUtil.getCurrentUserId());
        user.setRole(null);
        user.setPassword(null);
        userService.update(user);
        return Result.ok("更新成功");
    }

    @PutMapping("/change-password")
    public Result<?> changePassword(@Valid @RequestBody ChangePasswordDTO dto) {
        User currentUser = userService.getById(UserContextUtil.getCurrentUserId());
        if (!passwordEncoder.matches(dto.getOldPassword(), currentUser.getPassword())) {
            return Result.error("原密码错误");
        }
        currentUser.setPassword(dto.getNewPassword());
        userService.update(currentUser);
        return Result.ok("密码修改成功");
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<?> create(@RequestBody User user) {
        userService.create(user);
        return Result.ok("创建成功");
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<?> update(@PathVariable Long id, @RequestBody User user) {
        user.setId(id);
        userService.update(user);
        return Result.ok("更新成功");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<?> delete(@PathVariable Long id) {
        userService.delete(id);
        return Result.ok("删除成功");
    }
}
