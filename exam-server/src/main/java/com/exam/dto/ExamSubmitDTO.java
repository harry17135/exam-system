package com.exam.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@Data
public class ExamSubmitDTO {
    @NotNull(message = "考试记录ID不能为空")
    private Long recordId;
    private List<AnswerItem> answers;

    @Data
    public static class AnswerItem {
        private Long questionId;
        private String answer;
    }
}
