package com.yupi.springbootinit.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 题目
 */
@TableName(value = "question")
@Data
public class Question implements Serializable {

    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 问题
     */
    private String question;

    /**
     * 标签列表（json 数组）
     */
    private String tags;

    /**
     * 答案
     */
    private String answer;

    /**
     * 分析
     */
    private String analysis;

    /**
     * 创建用户 id
     */
    private Long userId;

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

    /**
     * 题目类别（副省级/地市级/行政执法）
     */
    private String category;

    /**
     * 题目类型（归纳概括/提出对策/综合分析/公文写作/作文）
     */
    private String type;

    /**
     * 题目话题（政治/经济/文化/生态/民生小事/基层治理）
     */
    private String topic;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
} 