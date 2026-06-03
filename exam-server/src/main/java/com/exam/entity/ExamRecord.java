package com.exam.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("exam_record")
public class ExamRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long arrangementId;
    private Long paperId;
    private Long studentId;
    private String status;      // IN_PROGRESS, FINISHED
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer totalScore;
    private Integer objectiveScore;
    private Integer isPassed;   // 0-否, 1-是
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
