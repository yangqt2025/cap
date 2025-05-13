package com.yupi.springbootinit.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户答题记录
 */
@TableName(value = "user_question_record")
@Data
public class UserQuestionRecord implements Serializable {

    /**
     * 记录ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

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
     * 内容得分
     */
    private Integer contentScore;

    /**
     * 格式得分
     */
    private Integer formatScore;

    /**
     * 逻辑得分
     */
    private Integer logicScore;

    /**
     * 语法得分
     */
    private Integer grammarScore;

    /**
     * 总分
     */
    private Double sum;

    /**
     * 建议
     */
    private String suggestion;

    /**
     * 整体建议
     */
    @TableField("overall_suggestion")
    private String overallSuggestion;

    /**
     * 分析优势
     */
    @TableField("analysis_strengths")
    private String analysisStrengths;

    /**
     * 分析改进
     */
    @TableField("analysis_improvements")
    private String analysisImprovements;

    /**
     * 具体建议
     */
    private String suggestions;

    /**
     * 题目类型（面试/申论）
     */
    @TableField("question_type")
    private String questionType;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否删除
     */
    @TableField("is_delete")
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
} 