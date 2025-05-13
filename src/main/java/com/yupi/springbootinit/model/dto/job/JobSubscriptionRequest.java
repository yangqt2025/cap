package com.yupi.springbootinit.model.dto.job;

import lombok.Data;

import java.io.Serializable;

/**
 * 岗位订阅请求
 */
@Data
public class JobSubscriptionRequest implements Serializable {

    /**
     * 用户ID
     */
    private Long userId;

    private static final long serialVersionUID = 1L;
} 