package com.yupi.springbootinit.model.dto.interview;

import lombok.Data;

@Data
public class SubmitAnswerRequest {
    /**
     * 题目ID
     */
    private Long questionId;

    /**
     * 用户答案
     */
    private String userAnswer;
} 