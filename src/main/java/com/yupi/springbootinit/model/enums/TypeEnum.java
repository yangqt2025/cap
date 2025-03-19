package com.yupi.springbootinit.model.enums;

public enum TypeEnum {
    SUMMARY("归纳概括"),
    STRATEGY("提出对策"),
    ANALYSIS("综合分析"),
    DOCUMENT_WRITING("公文写作"),
    ESSAY("作文");

    private final String value;

    TypeEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}