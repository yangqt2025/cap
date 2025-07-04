package com.yupi.springbootinit.model.vo;

import lombok.Data;
import java.io.Serializable;

/**
 * 面试题提交响应视图
 */
@Data
public class InterviewSubmitVO implements Serializable {

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
     * 答案ID
     */
    private Long answerId;

    /**
     * 计划能力得分 (content_score * 2)
     */
    private Integer plan;

    /**
     * 反应能力得分 (logic_score * 2)
     */
    private Integer reaction;

    /**
     * 表达能力得分 (form_score)
     */
    private Integer expression;

    /**
     * 人际关系得分 (grammar_score * 2)
     */
    private Integer relationship;

    /**
     * 综合能力得分
     */
    private Integer comprehensive;

    /**
     * 最终得分
     */
    private Integer finalScore;

    /**
     * 总体建议
     */
    private String suggestion;

    private static final long serialVersionUID = 1L;
} 