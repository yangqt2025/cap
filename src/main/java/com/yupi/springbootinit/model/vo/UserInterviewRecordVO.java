package com.yupi.springbootinit.model.vo;

import lombok.Data;
import java.io.Serializable;
import java.util.List;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;

/**
 * 用户面试答题记录视图
 */
@Data
public class UserInterviewRecordVO implements Serializable {

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
     * 题目内容
     */
    private String questionContent;

    /**
     * 题目类型
     */
    private String type;

    /**
     * 题目分类
     */
    private String category;

    /**
     * 出题模式
     */
    private String mode;

    /**
     * 用户答案
     */
    private String userAnswer;

    /**
     * 标准答案
     */
    private String standardAnswer;

    /**
     * 解析
     */
    private String analysis;

    /**
     * 内容得分
     */
    private Integer contentScore;

    /**
     * 逻辑得分
     */
    private Integer logicScore;

    /**
     * 形式得分
     */
    private Integer formScore;

    /**
     * 语法得分
     */
    private Integer grammarScore;

    /**
     * 格式得分
     */
    private Integer formatScore;

    /**
     * 详细反馈
     */
    private DetailedFeedback detailedFeedback;

    /**
     * 总体建议
     */
    private String overallSuggestion;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

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
    private Integer finalScore;

    @Data
    public static class DetailedFeedback {
        /**
         * 优点
         */
        private List<String> strengths;

        /**
         * 改进点
         */
        private List<String> areasForImprovement;

        /**
         * 具体建议
         */
        private List<String> specificSuggestions;
    }

    public Integer getFormatScore() {
        return formatScore;
    }

    public void setFormatScore(Integer formatScore) {
        this.formatScore = formatScore;
    }

    private static final long serialVersionUID = 1L;
} 