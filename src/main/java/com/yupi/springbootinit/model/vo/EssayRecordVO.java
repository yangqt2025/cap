package com.yupi.springbootinit.model.vo;

import lombok.Data;
import java.io.Serializable;
import java.util.List;

/**
 * 申论答题记录视图
 */
@Data
public class EssayRecordVO implements Serializable {

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
     * 详细反馈
     */
    private DetailedFeedback detailedFeedback;

    /**
     * 总体建议
     */
    private String overallSuggestion;

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
} 