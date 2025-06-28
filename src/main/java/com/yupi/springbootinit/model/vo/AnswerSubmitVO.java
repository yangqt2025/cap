package com.yupi.springbootinit.model.vo;

import lombok.Data;
import java.io.Serializable;

/**
 * 答题提交结果视图
 */
@Data
public class AnswerSubmitVO implements Serializable {

    /**
     * 记录ID
     */
    private Long recordId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 题目ID
     */
    private Long questionId;

    /**
     * 内容得分
     */
    private Integer contentScore;

    /**
     * 逻辑得分
     */
    private Integer logicScore;

    /**
     * 格式得分
     */
    private Integer formatScore;

    /**
     * 语法得分
     */
    private Integer grammarScore;

    /**
     * 最终得分
     */
    private Integer finalScore;

    private static final long serialVersionUID = 1L;
} 