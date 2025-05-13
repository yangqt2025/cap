package com.yupi.springbootinit.model.dto.userquestionrecord;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户答题记录查询请求
 */
@Data
public class UserQuestionRecordQueryRequest implements Serializable {

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 题目id
     */
    private Long questionId;

    private static final long serialVersionUID = 1L;
} 