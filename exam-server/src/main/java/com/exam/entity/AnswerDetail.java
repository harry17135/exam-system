package com.exam.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

@Data
@TableName("answer_detail")
public class AnswerDetail {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long examRecordId;
    private Long questionId;
    private String studentAnswer;
    private Integer isCorrect;  // 0-错误, 1-正确
    private Integer score;
    private Integer marked;     // 0-未批阅, 1-已批阅
}
