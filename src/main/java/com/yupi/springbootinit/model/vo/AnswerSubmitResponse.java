package com.yupi.springbootinit.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 提交答案响应
 */
@Data
public class AnswerSubmitResponse implements Serializable {

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
     * 最终得分
     */
    private Integer finalScore;

    /**
     * 详细反馈
     */
    private DetailedFeedback detailedFeedback;

    /**
     * 总体建议
     */
    private String overallSuggestion;

    /**
     * 详细反馈类
     */
    @Data
    public static class DetailedFeedback implements Serializable {
        /**
         * 优点
         */
        private List<String> strengths;

        /**
         * 需要改进的地方
         */
        private List<String> areasForImprovement;

        /**
         * 具体建议
         */
        private List<String> specificSuggestions;
    }

    private static final long serialVersionUID = 1L;
} 