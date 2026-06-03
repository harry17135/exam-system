package com.exam.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;

@Data
public class ExamStartDTO {
    @NotNull(message = "考试安排ID不能为空")
    private Long arrangementId;
}
