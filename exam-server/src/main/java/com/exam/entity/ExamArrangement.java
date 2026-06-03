package com.exam.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("exam_arrangement")
public class ExamArrangement {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long paperId;
    private Long classId;
    private String examName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long teacherId;
    private String status;      // PENDING, ONGOING, FINISHED
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}
