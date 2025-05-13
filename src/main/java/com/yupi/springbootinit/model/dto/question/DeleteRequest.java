package com.yupi.springbootinit.model.dto.question;

import lombok.Data;

import java.io.Serializable;

/**
 * 删除请求
 */
@Data
public class DeleteRequest implements Serializable {

    /**
     * 题目ID
     */
    private Long id;

    private static final long serialVersionUID = 1L;
} 