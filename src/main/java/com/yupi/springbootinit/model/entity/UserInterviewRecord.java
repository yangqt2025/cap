package com.yupi.springbootinit.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 用户面试答题记录
 */
@TableName(value = "user_interview_record")
@Data
public class UserInterviewRecord implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 用户id
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 题目id
     */
    @TableField("question_id")
    private Long questionId;

    /**
     * 用户答案
     */
    @TableField("user_answer")
    private String userAnswer;

    /**
     * 总分
     */
    private Integer score;

    /**
     * 反馈
     */
    @TableField("feedback")
    private String feedback;

    /**
     * 内容得分
     */
    @TableField("content_score")
    private Integer contentScore;

    /**
     * 逻辑得分
     */
    @TableField("logic_score")
    private Integer logicScore;

    /**
     * 格式得分
     */
    @TableField("form_score")
    private Integer formScore;

    /**
     * 语法得分
     */
    @TableField("grammar_score")
    private Integer grammarScore;

    /**
     * 优点
     */
    @TableField("strengths")
    private String strengths;

    /**
     * 改进点
     */
    @TableField("areas_for_improvement")
    private String areasForImprovement;

    /**
     * 具体建议
     */
    @TableField("specific_suggestions")
    private String specificSuggestions;

    /**
     * 总体建议
     */
    @TableField("overall_suggestion")
    private String overallSuggestion;

    /**
     * 计划得分
     */
    private Integer plan;

    /**
     * 反应得分
     */
    private Integer reaction;

    /**
     * 表达得分
     */
    private Integer expression;

    /**
     * 关系得分
     */
    private Integer relationship;

    /**
     * 综合得分
     */
    private Integer comprehensive;

    /**
     * 最终得分
     */
    @TableField("final_score")
    private Integer finalScore;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    /**
     * 是否删除
     */
    @TableField("is_delete")
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
