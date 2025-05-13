package com.yupi.springbootinit.model.vo;

import lombok.Data;
import java.io.Serializable;

/**
 * 面试题目视图
 */
@Data
public class InterviewQuestionVO implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * 题目
     */
    private String question;

    /**
     * 类型1
     */
    private String type1;

    /**
     * 题目分类
     */
    private String category;

    /**
     * 类型2
     */
    private String type2;

    /**
     * 答案
     */
    private String answer;

    /**
     * 解析
     */
    private String analysis;

    private static final long serialVersionUID = 1L;
} 