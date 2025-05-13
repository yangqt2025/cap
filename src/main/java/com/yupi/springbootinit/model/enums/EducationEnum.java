package com.yupi.springbootinit.model.enums;

import org.jetbrains.annotations.NotNull;

/**
 * 学历枚举
 */
public enum EducationEnum implements CharSequence {
    PRIMARY("小学", "PRIMARY"),
    JUNIOR("初中", "JUNIOR"),
    SENIOR("高中", "SENIOR"),
    COLLEGE("专科", "COLLEGE"),
    BACHELOR("本科", "BACHELOR"),
    MASTER("硕士", "MASTER"),
    DOCTOR("博士", "DOCTOR");

    private final String text;
    private final String value;

    EducationEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 获取枚举值（用于数据库存储）
     */
    public String getValue() {
        return this.value;
    }

    /**
     * 获取显示文本
     */
    public String getText() {
        return text;
    }

    /**
     * 根据文本获取枚举
     */
    public static EducationEnum getEnumByText(String text) {
        for (EducationEnum value : EducationEnum.values()) {
            if (value.getText().equals(text)) {
                return value;
            }
        }
        return null;
    }

    /**
     * 根据值获取枚举
     */
    public static EducationEnum getEnumByValue(String value) {
        for (EducationEnum enumValue : EducationEnum.values()) {
            if (enumValue.getValue().equals(value)) {
                return enumValue;
            }
        }
        return null;
    }

    @Override
    public int length() {
        return 0;
    }

    @Override
    public char charAt(int index) {
        return 0;
    }

    @NotNull
    @Override
    public CharSequence subSequence(int start, int end) {
        return null;
    }
}