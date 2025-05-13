package com.yupi.springbootinit.model.enums;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.yupi.springbootinit.model.enums.EnumValueDeserializer;

/**
 * 题目类别枚举
 */
@JsonDeserialize(using = EnumValueDeserializer.class)
public enum QuestionCategoryEnum implements BaseEnum {

    DEPUTY_PROVINCIAL("副省级"),
    PREFECTURE_CITY("地市级"),
    ADMINISTRATIVE_LAW("行政执法");

    private final String text;

    QuestionCategoryEnum(String text) {
        this.text = text;
    }

    @Override
    public String getText() {
        return text;
    }
} 