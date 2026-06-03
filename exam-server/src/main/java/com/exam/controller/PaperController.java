package com.exam.controller;

import com.exam.common.Result;
import com.exam.dto.PaperDTO;
import com.exam.service.PaperService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/papers")
@RequiredArgsConstructor
public class PaperController {

    private final PaperService paperService;

    @GetMapping
    public Result<?> list(@RequestParam(defaultValue = "1") Integer pageNum,
                          @RequestParam(defaultValue = "10") Integer pageSize,
                          @RequestParam(required = false) String keyword,
                          @RequestParam(required = false) String status) {
        return Result.ok(paperService.listPage(pageNum, pageSize, keyword, status));
    }

    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Long id) {
        return Result.ok(paperService.getById(id));
    }

    @GetMapping("/{id}/questions")
    public Result<?> getQuestions(@PathVariable Long id) {
        return Result.ok(paperService.getQuestionsByPaperId(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public Result<?> create(@Valid @RequestBody PaperDTO dto) {
        paperService.create(dto);
        return Result.ok("创建成功");
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public Result<?> update(@PathVariable Long id, @Valid @RequestBody PaperDTO dto) {
        paperService.update(id, dto);
        return Result.ok("更新成功");
    }

    @PutMapping("/{id}/publish")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public Result<?> publish(@PathVariable Long id) {
        paperService.publish(id);
        return Result.ok("发布成功");
    }

    @PutMapping("/{id}/close")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public Result<?> close(@PathVariable Long id) {
        paperService.close(id);
        return Result.ok("已关闭");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public Result<?> delete(@PathVariable Long id) {
        paperService.delete(id);
        return Result.ok("删除成功");
    }
}
