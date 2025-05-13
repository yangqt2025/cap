package com.yupi.springbootinit.model.dto.job;

import lombok.Data;

import java.io.Serializable;

/**
 * 职位添加请求
 */
@Data
public class JobAddRequest implements Serializable {

    /**
     * 职位名称
     */
    private String jobName;

    /**
     * 部门
     */
    private String department;

    /**
     * 单位
     */
    private String company;

    /**
     * 地区
     */
    private String region;

    /**
     * 是否编制(0-否,1-是)
     */
    private Boolean isCompiled;

    /**
     * 报名状态(0-未开始,1-已开始)
     */
    private Boolean applyStatus;

    /**
     * 竞争压力(0-10分)
     */
    private Integer competitionLevel;

    /**
     * 经费类型
     */
    private String fundingType;

    /**
     * 岗位
     */
    private String position;

    /**
     * 招聘人数
     */
    private Integer recruitNumber;

    /**
     * 联系人
     */
    private String contactPerson;

    /**
     * 联系电话
     */
    private String contactPhone;

    /**
     * 学历要求
     */
    private String education;

    /**
     * 学位要求
     */
    private String degree;

    /**
     * 专业要求
     */
    private String major;

    /**
     * 性别要求
     */
    private String gender;

    private static final long serialVersionUID = 1L;
} 