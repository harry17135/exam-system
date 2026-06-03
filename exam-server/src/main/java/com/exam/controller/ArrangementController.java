package com.exam.controller;

import com.exam.common.Result;
import com.exam.dto.ArrangementDTO;
import com.exam.service.ArrangementService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/arrangements")
@RequiredArgsConstructor
public class ArrangementController {

    private final ArrangementService arrangementService;

    @GetMapping
    public Result<?> list(@RequestParam(defaultValue = "1") Integer pageNum,
                          @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.ok(arrangementService.listPage(pageNum, pageSize));
    }

    @PostMapping
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public Result<?> create(@Valid @RequestBody ArrangementDTO dto) {
        arrangementService.create(dto);
        return Result.ok("安排成功");
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public Result<?> update(@PathVariable Long id, @Valid @RequestBody ArrangementDTO dto) {
        arrangementService.update(id, dto);
        return Result.ok("更新成功");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public Result<?> delete(@PathVariable Long id) {
        arrangementService.delete(id);
        return Result.ok("删除成功");
    }
}
