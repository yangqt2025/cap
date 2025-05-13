package com.yupi.springbootinit.model.dto.question;

import lombok.Data;

import java.io.Serializable;

/**
 * 创建题目请求
 */
@Data
public class QuestionAddRequest implements Serializable {

    /**
     * 题目标题
     */
    private String title;

    /**
     * 题目内容
     */
    private String content;

    /**
     * 题目答案
     */
    private String answer;

    /**
     * 题目分析
     */
    private String analysis;

    /**
     * 题目类别
     */
    private String category;

    /**
     * 题目类型
     */
    private String type;

    /**
     * 题目话题
     */
    private String topic;

    private static final long serialVersionUID = 1L;
} 