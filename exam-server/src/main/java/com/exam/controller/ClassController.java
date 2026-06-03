package com.exam.controller;

import com.exam.common.Result;
import com.exam.entity.ClassEntity;
import com.exam.service.ClassService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/classes")
@RequiredArgsConstructor
public class ClassController {

    private final ClassService classService;

    @GetMapping
    public Result<?> list(@RequestParam(defaultValue = "1") Integer pageNum,
                          @RequestParam(defaultValue = "10") Integer pageSize,
                          @RequestParam(required = false) String keyword) {
        return Result.ok(classService.listPage(pageNum, pageSize, keyword));
    }

    @GetMapping("/all")
    public Result<?> listAll() {
        return Result.ok(classService.listAll());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<?> create(@RequestBody ClassEntity entity) {
        classService.create(entity);
        return Result.ok("创建成功");
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<?> update(@PathVariable Long id, @RequestBody ClassEntity entity) {
        entity.setId(id);
        classService.update(entity);
        return Result.ok("更新成功");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<?> delete(@PathVariable Long id) {
        classService.delete(id);
        return Result.ok("删除成功");
    }
}
