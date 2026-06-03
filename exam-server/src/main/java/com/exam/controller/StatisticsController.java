package com.exam.controller;

import com.exam.common.Result;
import com.exam.service.GradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final GradeService gradeService;

    @GetMapping("/overview")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<?> overview() {
        return Result.ok(gradeService.getStatistics());
    }
}
