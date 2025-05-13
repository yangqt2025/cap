package com.yupi.springbootinit.model.enums;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.yupi.springbootinit.model.enums.EnumValueDeserializer;

/**
 * 题目话题枚举
 */
@JsonDeserialize(using = EnumValueDeserializer.class)
public enum QuestionTopicEnum implements BaseEnum {

    POLITICS("政治"),
    ECONOMY("经济"),
    CULTURE("文化"),
    ECOLOGY("生态"),
    LIVELIHOOD("民生小事"),
    GRASSROOTS_GOVERNANCE("基层治理");

    private final String text;

    QuestionTopicEnum(String text) {
        this.text = text;
    }

    @Override
    public String getText() {
        return text;
    }
} 