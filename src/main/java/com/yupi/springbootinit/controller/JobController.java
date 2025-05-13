package com.yupi.springbootinit.controller;

import com.yupi.springbootinit.common.BaseResponse;
import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.common.ResultUtils;
import com.yupi.springbootinit.exception.BusinessException;
import com.yupi.springbootinit.exception.ThrowUtils;
import com.yupi.springbootinit.model.dto.job.JobQueryRequest;
import com.yupi.springbootinit.model.dto.job.JobSubscriptionRequest;
import com.yupi.springbootinit.model.entity.Job;
import com.yupi.springbootinit.model.vo.JobVO;
import com.yupi.springbootinit.model.vo.JobSubscriptionScheduleVO;
import com.yupi.springbootinit.model.vo.UserJobSubscriptionVO;
import com.yupi.springbootinit.service.JobService;
import com.yupi.springbootinit.service.UserJobSubscriptionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.ApiOperation;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

/**
 * 岗位接口
 */
@RestController
@RequestMapping("/job")
@Slf4j
public class JobController {

    @Resource
    private JobService jobService;

    @Resource
    private UserJobSubscriptionService userJobSubscriptionService;

    /**
     * 获取岗位列表
     *
     * @param jobQueryRequest
     * @param request
     * @return
     */
    @RequestMapping(value = "/list", method = {RequestMethod.GET, RequestMethod.POST})
    public BaseResponse<List<JobVO>> listJobs(@RequestBody(required = false) JobQueryRequest jobQueryRequest,
                                            HttpServletRequest request) {
        return ResultUtils.success(jobService.getJobList(jobQueryRequest, request));
    }

    /**
     * 根据 id 获取岗位
     *
     * @param id
     * @param request
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<JobVO> getJobById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Job job = jobService.getById(id);
        if (job == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "岗位不存在");
        }
        return ResultUtils.success(jobService.getJobVO(job, request));
    }

    /**
     * 创建岗位
     *
     * @param job
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addJob(@RequestBody Job job, HttpServletRequest request) {
        if (job == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        jobService.validJob(job, true);
        boolean result = jobService.save(job);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(job.getId());
    }

    /**
     * 删除岗位
     *
     * @param id
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteJob(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = jobService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 更新岗位
     *
     * @param job
     * @param request
     * @return
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateJob(@RequestBody Job job, HttpServletRequest request) {
        if (job == null || job.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        jobService.validJob(job, false);
        boolean result = jobService.updateById(job);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 订阅岗位
     *
     * @param jobId
     * @param request
     * @return
     */
    @PostMapping("/subscribe")
    @ApiOperation("订阅岗位")
    public BaseResponse<Long> subscribeJob(@RequestParam Long jobId, @RequestHeader("userId") Long userId) {
        if (jobId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (userId == null || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户ID不能为空");
        }
        
        JobSubscriptionRequest subscriptionRequest = new JobSubscriptionRequest();
        subscriptionRequest.setUserId(userId);
        
        Long subscriptionId = userJobSubscriptionService.subscribeJob(jobId, subscriptionRequest);
        return ResultUtils.success(subscriptionId);
    }

    /**
     * 取消订阅
     *
     * @param request
     * @return
     */
    @PostMapping("/unsubscribe")
    public BaseResponse<Boolean> unsubscribeJob(HttpServletRequest request) {
        return ResultUtils.success(userJobSubscriptionService.unsubscribeJob(request));
    }

    /**
     * 获取用户的订阅信息
     *
     * @param request
     * @return
     */
    @GetMapping("/subscription")
    public BaseResponse<UserJobSubscriptionVO> getUserSubscription(HttpServletRequest request) {
        return ResultUtils.success(userJobSubscriptionService.getUserSubscription(request));
    }

    @GetMapping("/schedule")
    @ApiOperation(value = "获取用户订阅的工作时间表", notes = "通过请求头中的userId获取用户订阅的工作的考试日期和报名日期")
    public BaseResponse<List<JobSubscriptionScheduleVO>> getUserJobSchedule(@RequestHeader("userId") Long userId) {
        if (userId == null || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户ID不能为空");
        }
        List<JobSubscriptionScheduleVO> schedule = jobService.getUserJobSchedule(userId);
        return ResultUtils.success(schedule);
    }

    @PostMapping("/delete-account")
    @ApiOperation(value = "注销用户档案", notes = "删除用户的所有订阅记录、答题记录和用户资料")
    public BaseResponse<Boolean> deleteUserAccount(@RequestHeader("userId") Long userId) {
        if (userId == null || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户ID不能为空");
        }
        boolean result = jobService.deleteUserAccount(userId);
        return ResultUtils.success(result);
    }
} 