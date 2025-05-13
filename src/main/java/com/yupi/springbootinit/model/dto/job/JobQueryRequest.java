package com.yupi.springbootinit.model.dto.job;

import com.yupi.springbootinit.model.enums.EducationEnum;
import lombok.Data;

import java.io.Serializable;

/**
 * 职位查询请求
 */
@Data
public class JobQueryRequest implements Serializable {

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
    private String registrationStatus;

    /**
     * 竞争压力（压力大、压力一般）
     */
    private String competitionLevel;

    /**
     * 经费类型
     */
    private String fundingType;

    /**
     * 岗位
     */
    private String position;

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
     * 招聘人数
     */
    private Integer recruitNumber;

    /**
     * 获取学历枚举值
     */
    public EducationEnum getEducationEnum() {
        if (education == null) {
            return null;
        }
        return EducationEnum.getEnumByText(education);
    }

    private static final long serialVersionUID = 1L;
} 