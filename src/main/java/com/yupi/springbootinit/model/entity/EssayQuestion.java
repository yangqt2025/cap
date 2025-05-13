package com.yupi.springbootinit.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 申论题目
 */
@TableName(value = "essay_question")
@Data
public class EssayQuestion implements Serializable {

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 题目内容
     */
    private String question;

    /**
     * 题目类型1（套题模考/分类实战）
     */
    private String type1;

    /**
     * 题目类型2（文字出题/语音出题）
     */
    private String type2;

    /**
     * 标准答案
     */
    private String answer;

    /**
     * 答案解析
     */
    private String analysis;

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