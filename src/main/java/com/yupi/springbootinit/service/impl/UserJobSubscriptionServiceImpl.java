package com.yupi.springbootinit.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.common.UserHolder;
import com.yupi.springbootinit.exception.BusinessException;
import com.yupi.springbootinit.mapper.UserJobSubscriptionMapper;
import com.yupi.springbootinit.model.entity.Job;
import com.yupi.springbootinit.model.entity.UserJobSubscription;
import com.yupi.springbootinit.model.vo.JobVO;
import com.yupi.springbootinit.model.vo.UserJobSubscriptionVO;
import com.yupi.springbootinit.model.dto.job.JobSubscriptionRequest;
import com.yupi.springbootinit.service.JobService;
import com.yupi.springbootinit.service.UserJobSubscriptionService;
import com.yupi.springbootinit.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.beans.BeanUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * 用户岗位订阅服务实现类
 */
@Service
public class UserJobSubscriptionServiceImpl extends ServiceImpl<UserJobSubscriptionMapper, UserJobSubscription>
        implements UserJobSubscriptionService {

    @Autowired
    private UserJobSubscriptionMapper userJobSubscriptionMapper;

    @Autowired
    @Lazy
    private JobService jobService;

    @Autowired
    private UserService userService;

    @Override
    public Long subscribeJob(Long jobId, JobSubscriptionRequest request) {
        if (jobId == null || jobId <= 0 || request == null || request.getUserId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "岗位 ID 或用户 ID 参数有误");
        }

        // 校验岗位是否存在
        Job job = jobService.getById(jobId);
        if (job == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "岗位不存在");
        }

        Long userId = request.getUserId();

        // 查询是否存在包括逻辑删除的记录
        LambdaQueryWrapper<UserJobSubscription> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserJobSubscription::getUserId, userId)
                    .eq(UserJobSubscription::getJobId, jobId);

        UserJobSubscription existing = this.getOne(queryWrapper);

        if (existing != null) {
            // 如果存在记录，则切换删除状态
            existing.setIsDelete(existing.getIsDelete() == 0 ? 1 : 0);
            existing.setUpdateTime(new Date());
            this.updateById(existing);
            return existing.getId();
        }

        // 不存在，创建新记录
        UserJobSubscription subscription = new UserJobSubscription();
        subscription.setUserId(userId);
        subscription.setJobId(jobId);
        subscription.setIsDelete(0);
        subscription.setCreateTime(new Date());
        subscription.setUpdateTime(new Date());
        this.save(subscription);
        return subscription.getId();
    }

    @Override
    public boolean unsubscribeJob(HttpServletRequest request) {
        String userIdStr = UserHolder.getUser();
        if (userIdStr == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        Long userId = Long.parseLong(userIdStr);

        LambdaQueryWrapper<UserJobSubscription> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserJobSubscription::getUserId, userId);
        return this.remove(queryWrapper);
    }

    @Override
    public UserJobSubscriptionVO getUserSubscription(HttpServletRequest request) {
        String userIdStr = UserHolder.getUser();
        if (userIdStr == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        Long userId = Long.parseLong(userIdStr);

        // 使用LambdaQueryWrapper构建查询条件
        LambdaQueryWrapper<UserJobSubscription> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserJobSubscription::getUserId, userId)
                   .eq(UserJobSubscription::getIsDelete, 0);
        
        UserJobSubscription subscription = this.getOne(queryWrapper);
        if (subscription == null) {
            return null;
        }

        // 获取岗位信息
        Job job = jobService.getById(subscription.getJobId());
        if (job == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "岗位不存在");
        }

        // 转换为VO对象
        UserJobSubscriptionVO vo = UserJobSubscriptionVO.objToVo(subscription);
        vo.setJob(JobVO.objToVo(job));

        return vo;
    }
}
