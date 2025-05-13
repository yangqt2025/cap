package com.yupi.springbootinit.model.vo;

import com.yupi.springbootinit.model.entity.Job;
import com.yupi.springbootinit.model.enums.EducationEnum;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * 岗位视图对象
 */
@Data
public class JobVO implements Serializable {

    /**
     * id
     */
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
     * 地区
     */
    private String region;

    /**
     * 是否编制(0-否,1-是)
     */
    private Integer isCompiled;

    /**
     * 报名状态(0-未开始,1-已开始)
     */
    private Integer applyStatus;

    /**
     * 竞争压力(0-10分)
     */
    private Integer competitionLevel;

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
     * 报名状态：未开始、开始报名、已结束
     */
    private String registrationStatus;

    /**
     * 考试日期
     */
    private Date examDate;

    /**
     * 报名日期
     */
    private Date registrationDate;

    /**
     * 岗位详情URL
     */
    private String url;

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
     * @param job 岗位信息
     * @return 岗位视图对象
     */
    public static JobVO objToVo(Job job) {
        if (job == null) {
            return null;
        }
        JobVO jobVO = new JobVO();
        BeanUtils.copyProperties(job, jobVO);
        return jobVO;
    }
} 