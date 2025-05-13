package com.yupi.springbootinit.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 岗位报名状态枚举
 */
public enum JobRegistrationStatusEnum {
    NOT_STARTED("未开始"),
    IN_PROGRESS("开始报名"),
    ENDED("已结束");

    private final String text;

    JobRegistrationStatusEnum(String text) {
        this.text = text;
    }

    @JsonValue
    public String getText() {
        return text;
    }
} 