package com.yupi.springbootinit.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yupi.springbootinit.model.entity.UserJobSubscription;
import com.yupi.springbootinit.model.vo.UserJobSubscriptionVO;
import com.yupi.springbootinit.model.dto.job.JobSubscriptionRequest;

import javax.servlet.http.HttpServletRequest;

/**
 * 用户岗位订阅服务
 */
public interface UserJobSubscriptionService extends IService<UserJobSubscription> {

    /**
     * 订阅岗位
     *
     * @param jobId 岗位ID
     * @param request 请求
     * @return 订阅ID
     */
    Long subscribeJob(Long jobId, JobSubscriptionRequest request);

    /**
     * 取消订阅
     *
     * @param request 请求
     * @return 是否成功
     */
    boolean unsubscribeJob(HttpServletRequest request);

    /**
     * 获取用户的订阅信息
     *
     * @param request 请求
     * @return 订阅信息
     */
    UserJobSubscriptionVO getUserSubscription(HttpServletRequest request);
} 