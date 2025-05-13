package com.yupi.springbootinit.model.enums;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.yupi.springbootinit.model.enums.EnumValueDeserializer;

/**
 * 题目类型枚举
 */
@JsonDeserialize(using = EnumValueDeserializer.class)
public enum QuestionTypeEnum implements BaseEnum {

    SUMMARY("归纳概括"),
    COUNTERMEASURE("提出对策"),
    COMPREHENSIVE_ANALYSIS("综合分析"),
    OFFICIAL_DOCUMENT("公文写作"),
    ESSAY("作文");

    private final String text;

    QuestionTypeEnum(String text) {
        this.text = text;
    }

    @Override
    public String getText() {
        return text;
    }
} 