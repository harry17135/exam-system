package com.exam.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@Data
public class PaperDTO {
    @NotBlank(message = "试卷名称不能为空")
    private String paperName;
    private String description;
    @NotNull(message = "考试时长不能为空")
    private Integer duration;
    @NotNull(message = "总分不能为空")
    private Integer totalScore;
    @NotNull(message = "及格分数不能为空")
    private Integer passScore;
    private List<QuestionItem> questions;

    @Data
    public static class QuestionItem {
        private Long questionId;
        private Integer score;
        private Integer sortOrder;
    }
}
