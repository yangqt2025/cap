package com.yupi.springbootinit.model.dto.userquestionrecord;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户做题记录添加请求
 */
@Data
public class UserQuestionRecordAddRequest implements Serializable {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 题目ID
     */
    private Long questionId;

    /**
     * 用户答案
     */
    private String userAnswer;

    /**
     * 内容按点给分(0-10分)
     */
    private Integer contentScore;

    /**
     * 格式得分(0-10分)
     */
    private Integer formatScore;

    /**
     * 语言逻辑得分(0-10分)
     */
    private Integer logicScore;

    /**
     * 语法和段落衔接得分(0-10分)
     */
    private Integer grammarScore;

    private static final long serialVersionUID = 1L;
} 