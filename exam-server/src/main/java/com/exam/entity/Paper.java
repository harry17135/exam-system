package com.exam.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("paper")
public class Paper {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String paperName;
    private String description;
    private Integer duration;       // 考试时长(分钟)
    private Integer totalScore;
    private Integer passScore;
    private String status;          // DRAFT, PUBLISHED, CLOSED
    private Long teacherId;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}
