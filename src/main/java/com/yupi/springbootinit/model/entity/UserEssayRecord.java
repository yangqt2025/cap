package com.yupi.springbootinit.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户申论答题记录
 */
@TableName(value = "user_essay_record")
@Data
public class UserEssayRecord implements Serializable {

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 题目id
     */
    private Long questionId;

    /**
     * 用户答案
     */
    private String userAnswer;

    /**
     * 总分
     */
    private Integer score;

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
     * 反馈信息（JSON格式）
     */
    private String feedback;

    /**
     * 总体建议
     */
    private String overallSuggestion;

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
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
} 