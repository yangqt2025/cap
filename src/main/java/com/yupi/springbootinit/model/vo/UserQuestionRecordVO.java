package com.yupi.springbootinit.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 用户做题记录视图
 */
@Data
public class UserQuestionRecordVO implements Serializable {

    /**
     * 记录ID
     */
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
     * 题目标题
     */
    private String questionTitle;

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

    /**
     * 做题时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date updateTime;

    /**
     * 总分
     */
    private BigDecimal totalScore;

    /**
     * 标准答案
     */
    private String standardAnswer;

    /**
     * 答案分析
     */
    private String analysis;

    /**
     * 题目内容
     */
    private String questionContent;

    /**
     * 题目类型（面试/申论）
     */
    private String questionType;
    
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

    /**
     * 总分
     */
    private Double sum;

    /**
     * 总体建议
     */
    private String overallSuggestion;

    /**
     * 详细反馈
     */
    private DetailedFeedback detailedFeedback;

    /**
     * 优点
     */
    private String strengths;

    /**
     * 改进点
     */
    private String areasForImprovement;

    /**
     * 具体建议
     */
    private String specificSuggestions;

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

    private static final long serialVersionUID = 1L;

    public Double getSum() {
        return sum;
    }

    public void setSum(Double sum) {
        this.sum = sum;
    }
} 