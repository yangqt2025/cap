package com.yupi.springbootinit.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yupi.springbootinit.model.dto.job.JobAddRequest;
import com.yupi.springbootinit.model.dto.job.JobQueryRequest;
import com.yupi.springbootinit.model.entity.Job;
import com.yupi.springbootinit.model.vo.JobVO;
import com.yupi.springbootinit.model.vo.JobSubscriptionScheduleVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 职位服务
 */
public interface JobService extends IService<Job> {

    /**
     * 校验岗位信息
     *
     * @param job 岗位信息
     * @param add 是否为新增校验
     */
    void validJob(Job job, boolean add);

    /**
     * 获取查询条件
     *
     * @param jobQueryRequest 查询请求
     * @return 查询条件
     */
    QueryWrapper<Job> getQueryWrapper(JobQueryRequest jobQueryRequest);

    /**
     * 获取岗位视图对象
     *
     * @param job 岗位信息
     * @param request 请求
     * @return 岗位视图对象
     */
    JobVO getJobVO(Job job, HttpServletRequest request);

    /**
     * 获取岗位列表
     *
     * @param jobQueryRequest 查询请求
     * @param request 请求
     * @return 岗位列表
     */
    List<JobVO> getJobList(JobQueryRequest jobQueryRequest, HttpServletRequest request);

    /**
     * 获取职位列表
     *
     * @param jobQueryRequest 查询请求
     * @return 职位列表
     */
    List<JobVO> listJobs(JobQueryRequest jobQueryRequest);

    /**
     * 获取职位
     *
     * @param id 职位ID
     * @return 职位
     */
    JobVO getJobById(Long id);

    /**
     * 添加职位
     *
     * @param jobAddRequest 添加请求
     * @return 职位ID
     */
    Long addJob(JobAddRequest jobAddRequest);

    /**
     * 删除岗位
     *
     * @param id 岗位ID
     * @return 是否删除成功
     */
    boolean deleteJob(Long id);

    /**
     * 更新岗位
     *
     * @param job 岗位信息
     * @return 是否更新成功
     */
    boolean updateJob(Job job);

    /**
     * 更新岗位报名状态
     */
    void updateRegistrationStatus();

    /**
     * 获取职位列表
     *
     * @param jobQueryRequest 查询请求
     * @return 职位列表
     */
    List<Job> listJob(JobQueryRequest jobQueryRequest);

    /**
     * 获取用户订阅的工作时间表
     * @param userId 用户ID
     * @return 工作订阅时间表列表
     */
    List<JobSubscriptionScheduleVO> getUserJobSchedule(Long userId);

    /**
     * 删除用户档案
     * @param userId 用户ID
     * @return 是否删除成功
     */
    boolean deleteUserAccount(Long userId);

    boolean deleteAccount(Long userId);
} 