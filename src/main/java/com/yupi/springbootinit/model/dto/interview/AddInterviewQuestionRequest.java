package com.yupi.springbootinit.model.dto.interview;

import lombok.Data;

@Data
public class AddInterviewQuestionRequest {
    /**
     * 题目内容
     */
    private String question;

    /**
     * 题目类型（套题模考/分类实战）
     */
    private String type;

    /**
     * 题目分类（综合分析/计划组织/人际关系/紧急应变/情景模拟）
     */
    private String category;

    /**
     * 出题模式（文字出题/语音出题）
     */
    private String mode;

    /**
     * 标准答案
     */
    private String answer;

    /**
     * 答案解析
     */
    private String analysis;
} 