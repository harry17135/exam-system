package com.exam.vo;

import lombok.Data;

@Data
public class StatisticsVO {
    private long totalStudents;
    private long totalTeachers;
    private long totalQuestions;
    private long totalPapers;
    private long totalExams;
    private double averageScore;
    private double passRate;
}
