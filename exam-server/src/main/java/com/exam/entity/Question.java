package com.exam.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("question")
public class Question {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String type;        // SINGLE, MULTI, JUDGE, ESSAY
    private String title;
    private String options;     // JSON: [{"label":"A","content":"..."}]
    private String answer;
    private String analysis;
    private Integer score;
    private Integer difficulty; // 1-5
    private Long teacherId;
    private Integer status;     // 0-禁用, 1-正常
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}
