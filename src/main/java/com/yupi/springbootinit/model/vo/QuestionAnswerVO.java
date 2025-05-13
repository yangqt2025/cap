package com.yupi.springbootinit.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 题目答案和点评视图
 */
@Data
public class QuestionAnswerVO implements Serializable {

    /**
     * 题目 id
     */
    private Long questionId;

    /**
     * 正确答案
     */
    private String answer;

    /**
     * 答案分析
     */
    private String analysis;

    /**
     * 用户提交的答案
     */
    private String userAnswer;

    /**
     * 是否正确
     */
    private Boolean correct;

    private static final long serialVersionUID = 1L;
} 