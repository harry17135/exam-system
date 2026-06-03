package com.exam.controller;

import com.exam.common.Result;
import com.exam.entity.Question;
import com.exam.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    @GetMapping
    public Result<?> list(@RequestParam(defaultValue = "1") Integer pageNum,
                          @RequestParam(defaultValue = "10") Integer pageSize,
                          @RequestParam(required = false) String type,
                          @RequestParam(required = false) String keyword,
                          @RequestParam(required = false) Integer difficulty) {
        return Result.ok(questionService.listPage(pageNum, pageSize, type, keyword, difficulty));
    }

    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Long id) {
        return Result.ok(questionService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public Result<?> create(@RequestBody Question question) {
        questionService.create(question);
        return Result.ok("创建成功");
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public Result<?> update(@PathVariable Long id, @RequestBody Question question) {
        question.setId(id);
        questionService.update(question);
        return Result.ok("更新成功");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public Result<?> delete(@PathVariable Long id) {
        questionService.delete(id);
        return Result.ok("删除成功");
    }
}
