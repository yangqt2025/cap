package com.yupi.springbootinit.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.exception.BusinessException;
import com.yupi.springbootinit.exception.ThrowUtils;
import com.yupi.springbootinit.mapper.JobMapper;
import com.yupi.springbootinit.mapper.UserJobSubscriptionMapper;
import com.yupi.springbootinit.mapper.UserQuestionRecordMapper;
import com.yupi.springbootinit.mapper.UserInterviewRecordMapper;
import com.yupi.springbootinit.model.dto.job.JobAddRequest;
import com.yupi.springbootinit.model.dto.job.JobQueryRequest;
import com.yupi.springbootinit.model.entity.Job;
import com.yupi.springbootinit.model.entity.User;
import com.yupi.springbootinit.model.entity.UserJobSubscription;
import com.yupi.springbootinit.model.entity.UserQuestionRecord;
import com.yupi.springbootinit.model.entity.UserInterviewRecord;
import com.yupi.springbootinit.model.enums.JobRegistrationStatusEnum;
import com.yupi.springbootinit.model.vo.JobSubscriptionScheduleVO;
import com.yupi.springbootinit.model.vo.JobVO;
import com.yupi.springbootinit.service.JobService;
import com.yupi.springbootinit.service.UserJobSubscriptionService;
import com.yupi.springbootinit.service.UserQuestionRecordService;
import com.yupi.springbootinit.service.UserService;
import com.yupi.springbootinit.service.UserInterviewRecordService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList;

/**
 * 岗位服务实现类
 */
@Service
public class JobServiceImpl extends ServiceImpl<JobMapper, Job> implements JobService {

    private static final Logger log = LoggerFactory.getLogger(JobServiceImpl.class);

    @Resource
    private JobMapper jobMapper;

    @Resource
    @Lazy
    private UserJobSubscriptionService userJobSubscriptionService;

    @Resource
    private UserQuestionRecordService userQuestionRecordService;

    @Resource
    private UserService userService;

    @Resource
    private UserInterviewRecordMapper userInterviewRecordMapper;

    @Resource
    private UserQuestionRecordMapper userQuestionRecordMapper;

    @Resource
    private UserInterviewRecordService userInterviewRecordService;

    @Override
    public void validJob(Job job, boolean add) {
        if (job == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String jobName = job.getJobName();
        String department = job.getDepartment();
        String company = job.getCompany();
        String fundingType = job.getFundingType();
        String position = job.getPosition();
        Integer recruitNumber = job.getRecruitNumber();
        String contactPerson = job.getContactPerson();
        String contactPhone = job.getContactPhone();
        String education = job.getEducation();
        String degree = job.getDegree();
        String major = job.getMajor();
        String gender = job.getGender();
        // 创建时，所有参数必须非空
        if (add) {
            ThrowUtils.throwIf(StringUtils.isBlank(jobName), ErrorCode.PARAMS_ERROR, "岗位名称不能为空");
            ThrowUtils.throwIf(StringUtils.isBlank(department), ErrorCode.PARAMS_ERROR, "主管部门不能为空");
            ThrowUtils.throwIf(StringUtils.isBlank(company), ErrorCode.PARAMS_ERROR, "招聘单位不能为空");
            ThrowUtils.throwIf(StringUtils.isBlank(fundingType), ErrorCode.PARAMS_ERROR, "经费形式不能为空");
            ThrowUtils.throwIf(StringUtils.isBlank(position), ErrorCode.PARAMS_ERROR, "招录职位不能为空");
            ThrowUtils.throwIf(recruitNumber == null || recruitNumber <= 0, ErrorCode.PARAMS_ERROR, "招录数量必须大于0");
            ThrowUtils.throwIf(StringUtils.isBlank(contactPerson), ErrorCode.PARAMS_ERROR, "联系人不能为空");
            ThrowUtils.throwIf(StringUtils.isBlank(contactPhone), ErrorCode.PARAMS_ERROR, "联系电话不能为空");
            ThrowUtils.throwIf(StringUtils.isBlank(education), ErrorCode.PARAMS_ERROR, "学历要求不能为空");
            ThrowUtils.throwIf(StringUtils.isBlank(degree), ErrorCode.PARAMS_ERROR, "学位要求不能为空");
            ThrowUtils.throwIf(StringUtils.isBlank(major), ErrorCode.PARAMS_ERROR, "专业要求不能为空");
            ThrowUtils.throwIf(StringUtils.isBlank(gender), ErrorCode.PARAMS_ERROR, "性别要求不能为空");
        }
    }

    @Override
    public QueryWrapper<Job> getQueryWrapper(JobQueryRequest jobQueryRequest) {
        QueryWrapper<Job> queryWrapper = new QueryWrapper<>();
        if (jobQueryRequest == null) {
            return queryWrapper;
        }

        // 添加删除标记条件
        queryWrapper.eq("isDelete", 0);

        // 地区精确匹配
        if (StringUtils.isNotBlank(jobQueryRequest.getRegion())) {
            String normalizedRegion = normalizeRegion(jobQueryRequest.getRegion());
            queryWrapper.eq("region", normalizedRegion);
            log.info("查询地区: {}, 标准化后: {}", jobQueryRequest.getRegion(), normalizedRegion);
        }

        // 是否编制精确匹配
        if (jobQueryRequest.getIsCompiled() != null) {
            queryWrapper.eq("isCompiled", jobQueryRequest.getIsCompiled());
            log.info("查询是否编制: {}", jobQueryRequest.getIsCompiled());
        }

        // 报名状态精确匹配
        if (StringUtils.isNotBlank(jobQueryRequest.getRegistrationStatus())) {
            queryWrapper.eq("registration_status", jobQueryRequest.getRegistrationStatus());
            log.info("查询报名状态: {}", jobQueryRequest.getRegistrationStatus());
        }

        // 竞争压力精确匹配
        if (StringUtils.isNotBlank(jobQueryRequest.getCompetitionLevel())) {
            queryWrapper.eq("competitionLevel", jobQueryRequest.getCompetitionLevel());
            log.info("查询竞争压力: {}", jobQueryRequest.getCompetitionLevel());
        }

        // 按创建时间降序排序
        queryWrapper.orderByDesc("createTime");
        
        return queryWrapper;
    }

    /**
     * 标准化地区名称
     * @param region 原始地区名称
     * @return 标准化后的地区名称
     */
    private String normalizeRegion(String region) {
        if (region == null) {
            return null;
        }
        // 移除"市"字
        return region.replace("市", "").trim();
    }

    @Override
    public JobVO getJobVO(Job job, HttpServletRequest request) {
        if (job == null) {
            return null;
        }
        JobVO jobVO = new JobVO();
        BeanUtils.copyProperties(job, jobVO);
        return jobVO;
    }

    @Override
    public List<JobVO> getJobList(JobQueryRequest jobQueryRequest, HttpServletRequest request) {
        QueryWrapper<Job> queryWrapper = getQueryWrapper(jobQueryRequest);
        List<Job> jobList = this.list(queryWrapper);
        return jobList.stream().map(job -> getJobVO(job, request)).collect(Collectors.toList());
    }

    @Override
    public List<JobVO> listJobs(JobQueryRequest jobQueryRequest) {
        QueryWrapper<Job> queryWrapper = getQueryWrapper(jobQueryRequest);
        List<Job> jobList = this.list(queryWrapper);
        return jobList.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    public JobVO getJobById(Long id) {
        Job job = this.getById(id);
        return job != null ? convertToVO(job) : null;
    }

    @Override
    public Long addJob(JobAddRequest jobAddRequest) {
        Job job = new Job();
        BeanUtils.copyProperties(jobAddRequest, job);
        // 校验参数
        validJob(job, true);
        // 保存
        save(job);
        return job.getId();
    }

    @Override
    public boolean deleteJob(Long id) {
        return this.removeById(id);
    }

    @Override
    public boolean updateJob(Job job) {
        return this.updateById(job);
    }

    @Override
    public void updateRegistrationStatus() {
        // 获取所有岗位
        List<Job> jobList = this.list();
        Date now = new Date();
        
        for (Job job : jobList) {
            Date examDate = job.getExamDate();
            Date registrationDate = job.getRegistrationDate();
            String currentStatus = job.getRegistrationStatus();
            
            // 如果考试日期已过，状态设为"已结束"
            if (examDate != null && now.after(examDate)) {
                job.setRegistrationStatus("已结束");
            }
            // 如果报名日期已到但考试日期未到，状态设为"开始报名"
            else if (registrationDate != null && now.after(registrationDate) && 
                    (examDate == null || now.before(examDate))) {
                job.setRegistrationStatus("开始报名");
            }
            // 如果报名日期未到，状态设为"未开始"
            else if (registrationDate != null && now.before(registrationDate)) {
                job.setRegistrationStatus("未开始");
            }
            
            // 如果状态发生变化，更新数据库
            if (!currentStatus.equals(job.getRegistrationStatus())) {
                this.updateById(job);
            }
        }
    }

    /**
     * 转换为视图对象
     */
    private JobVO convertToVO(Job job) {
        if (job == null) {
            return null;
        }
        JobVO jobVO = new JobVO();
        BeanUtils.copyProperties(job, jobVO);
        return jobVO;
    }

    @Override
    public List<Job> listJob(JobQueryRequest jobQueryRequest) {
        QueryWrapper<Job> queryWrapper = getQueryWrapper(jobQueryRequest);
        return jobMapper.selectList(queryWrapper);
    }

    @Override
    public List<JobSubscriptionScheduleVO> getUserJobSchedule(Long userId) {
        if (userId == null || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户ID不能为空");
        }
        
        // 使用userJobSubscriptionService的方法获取订阅记录
        List<UserJobSubscription> subscriptions = userJobSubscriptionService.list(
            new QueryWrapper<UserJobSubscription>()
                .eq("userId", userId)
        );
        
        // 如果没有订阅，返回空列表
        if (subscriptions.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 获取所有订阅的工作ID
        List<Long> jobIds = subscriptions.stream()
                .map(UserJobSubscription::getJobId)
                .collect(Collectors.toList());
        
        // 查询工作信息
        QueryWrapper<Job> jobQueryWrapper = new QueryWrapper<>();
        jobQueryWrapper.in("id", jobIds);
        List<Job> jobs = this.list(jobQueryWrapper);
        
        // 转换为VO对象
        return jobs.stream().map(job -> {
            JobSubscriptionScheduleVO vo = new JobSubscriptionScheduleVO();
            vo.setJobId(job.getId());
            vo.setJobName(job.getJobName());
            vo.setDepartment(job.getDepartment());
            vo.setCompany(job.getCompany());
            vo.setRegion(job.getRegion());
            vo.setExamDate(job.getExamDate());
            vo.setRegistrationDate(job.getRegistrationDate());
            vo.setRegistrationStatus(job.getRegistrationStatus());
            
            // 设置订阅ID
            subscriptions.stream()
                    .filter(sub -> sub.getJobId().equals(job.getId()))
                    .findFirst()
                    .ifPresent(sub -> vo.setId(sub.getId()));
            
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public boolean deleteUserAccount(Long userId) {
        if (userId == null || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户ID不能为空");
        }

        // 1. 删除用户订阅记录
        QueryWrapper<UserJobSubscription> subscriptionQueryWrapper = new QueryWrapper<>();
        subscriptionQueryWrapper.eq("userId", userId);
        userJobSubscriptionService.remove(subscriptionQueryWrapper);

        // 2. 删除用户答题记录
        QueryWrapper<UserQuestionRecord> recordQueryWrapper = new QueryWrapper<>();
        recordQueryWrapper.eq("userId", userId);
        userQuestionRecordService.remove(recordQueryWrapper);

        // 3. 删除用户资料
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("id", userId);
        userService.remove(userQueryWrapper);

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteAccount(Long userId) {
        if (userId == null || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        // 使用逻辑删除方法删除用户记录
        QueryWrapper<UserInterviewRecord> interviewWrapper = new QueryWrapper<>();
        interviewWrapper.eq("user_id", userId);
        int interviewResult = userInterviewRecordMapper.update(null, 
            new com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper<UserInterviewRecord>()
                .eq("user_id", userId)
                .set("is_delete", 1));
        log.info("删除用户面试记录结果: {}", interviewResult);

        // 删除用户答题记录
        QueryWrapper<UserQuestionRecord> questionWrapper = new QueryWrapper<>();
        questionWrapper.eq("userId", userId);
        int questionResult = userQuestionRecordMapper.update(null,
            new com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper<UserQuestionRecord>()
                .eq("userId", userId)
                .set("is_delete", 1));
        log.info("删除用户答题记录结果: {}", questionResult);

        // 删除用户订阅记录
        QueryWrapper<UserJobSubscription> subscriptionWrapper = new QueryWrapper<>();
        subscriptionWrapper.eq("userId", userId);
        boolean subscriptionResult = userJobSubscriptionService.update(null,
            new com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper<UserJobSubscription>()
                .eq("userId", userId)
                .set("isDelete", 1));
        log.info("删除用户订阅记录结果: {}", subscriptionResult);

        return true;
    }
} 
