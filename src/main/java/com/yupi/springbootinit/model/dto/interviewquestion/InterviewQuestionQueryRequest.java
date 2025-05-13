package com.yupi.springbootinit.model.dto.interviewquestion;

import com.yupi.springbootinit.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 面试题目查询请求
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class InterviewQuestionQueryRequest extends PageRequest implements Serializable {

    /**
     * 题目类型1
     */
    private String type1;

    /**
     * 题目类型2
     */
    private String type2;

    private static final long serialVersionUID = 1L;
} 