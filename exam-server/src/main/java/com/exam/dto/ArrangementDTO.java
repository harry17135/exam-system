package com.exam.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;

@Data
public class ArrangementDTO {
    @NotNull(message = "试卷ID不能为空")
    private Long paperId;
    @NotNull(message = "班级ID不能为空")
    private Long classId;
    private String examName;
    @NotNull(message = "开始时间不能为空")
    private String startTime;
    @NotNull(message = "结束时间不能为空")
    private String endTime;
}
