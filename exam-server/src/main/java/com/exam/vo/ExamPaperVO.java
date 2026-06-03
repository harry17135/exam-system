package com.exam.vo;

import lombok.Data;
import java.util.List;

@Data
public class ExamPaperVO {
    private Long recordId;
    private Long paperId;
    private String paperName;
    private String description;
    private Integer duration;
    private Integer totalScore;
    private Integer passScore;
    private List<QuestionVO> questions;

    @Data
    public static class QuestionVO {
        private Long questionId;
        private String type;
        private String title;
        private String options;
        private Integer score;
        private Integer sortOrder;
    }
}
