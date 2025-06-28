package com.yupi.springbootinit.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 工作订阅时间表视图
 */
@Data
public class JobSubscriptionScheduleVO implements Serializable {

    /**
     * 订阅ID
     */
    private Long id;

    /**
     * 工作ID
     */
    private Long jobId;

    /**
     * 岗位名称
     */
    private String jobName;

    /**
     * 主管部门
     */
    private String department;

    /**
     * 招聘单位
     */
    private String company;

    /**
     * 地区
     */
    private String region;

    /**
     * 考试日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date examDate;

    /**
     * 报名日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date registrationDate;

    /**
     * 报名状态
     */
    private String registrationStatus;

    /**
     * 岗位详情URL
     */
    private String url;

    private static final long serialVersionUID = 1L;
} 