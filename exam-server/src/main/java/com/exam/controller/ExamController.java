package com.exam.controller;

import com.exam.common.Result;
import com.exam.dto.ExamStartDTO;
import com.exam.dto.ExamSubmitDTO;
import com.exam.service.ExamService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/exam")
@RequiredArgsConstructor
public class ExamController {

    private final ExamService examService;

    @PostMapping("/start")
    public Result<?> start(@Valid @RequestBody ExamStartDTO dto) {
        return Result.ok("开始考试", examService.startExam(dto));
    }

    @PostMapping("/submit")
    public Result<?> submit(@Valid @RequestBody ExamSubmitDTO dto) {
        int score = examService.submitExam(dto);
        return Result.ok("交卷成功，客观题得分: " + score, score);
    }

    @GetMapping("/records")
    public Result<?> myRecords(@RequestParam(defaultValue = "1") Integer pageNum,
                               @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.ok(examService.getMyRecords(pageNum, pageSize));
    }

    @GetMapping("/record/{id}")
    public Result<?> recordDetail(@PathVariable Long id) {
        return Result.ok(examService.getRecordDetail(id));
    }

    @GetMapping("/record/{id}/answers")
    public Result<?> answerDetails(@PathVariable Long id) {
        return Result.ok(examService.getAnswerDetails(id));
    }

    @PostMapping("/grade/{recordId}/{questionId}")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public Result<?> grade(@PathVariable Long recordId, @PathVariable Long questionId, @RequestParam Integer score) {
        examService.gradeEssay(recordId, questionId, score);
        return Result.ok("批阅成功");
    }
}
