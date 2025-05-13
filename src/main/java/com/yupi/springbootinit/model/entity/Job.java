package com.yupi.springbootinit.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.yupi.springbootinit.config.EducationEnumTypeHandler;
import com.yupi.springbootinit.model.enums.EducationEnum;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 岗位信息
 */
@TableName(value = "job")
@Data
public class Job implements Serializable {

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

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
     * 地区（北京、上海、广州、深圳、成都、杭州等）
     */
    private String region;

    /**
     * 是否编制(0-否,1-是)
     */
    private Integer isCompiled;

    /**
     * 报名状态（未开始、开始报名、已结束）
     */
    @TableField("registration_status")
    private String registrationStatus;

    /**
     * 竞争压力（压力大、压力一般）
     */
    private String competitionLevel;

    /**
     * 经费形式
     */
    private String fundingType;

    /**
     * 招录职位
     */
    private String position;

    /**
     * 招录数量
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

    /**
     * 考试日期
     */
    @TableField("exam_date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date examDate;

    /**
     * 报名日期
     */
    @TableField("registration_date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date registrationDate;

    /**
     * 岗位详情URL
     */
    private String url;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    /**
     * 是否删除
     */
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
} 