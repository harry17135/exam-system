package com.exam.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ExamRecordVO {
    private Long id;
    private Long arrangementId;
    private Long paperId;
    private String paperName;
    private String status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer totalScore;
    private Integer objectiveScore;
    private Integer isPassed;
    private String examName;
}
