package com.yupi.springbootinit.model.vo;

import com.yupi.springbootinit.model.entity.Job;
import com.yupi.springbootinit.model.entity.UserJobSubscription;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户岗位订阅视图
 */
@Data
public class UserJobSubscriptionVO implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 岗位id
     */
    private Long jobId;

    /**
     * 岗位信息
     */
    private JobVO job;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    private static final long serialVersionUID = 1L;

    /**
     * 对象转包装类
     *
     * @param subscription
     * @return
     */
    public static UserJobSubscriptionVO objToVo(UserJobSubscription subscription) {
        if (subscription == null) {
            return null;
        }
        UserJobSubscriptionVO subscriptionVO = new UserJobSubscriptionVO();
        subscriptionVO.setId(subscription.getId());
        subscriptionVO.setUserId(subscription.getUserId());
        subscriptionVO.setJobId(subscription.getJobId());
        subscriptionVO.setCreateTime(subscription.getCreateTime());
        subscriptionVO.setUpdateTime(subscription.getUpdateTime());
        return subscriptionVO;
    }
} 