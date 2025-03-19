package com.yupi.springbootinit.model.enums;

public enum ContentEnum {
    POLITICS("政治"),
    ECONOMY("经济"),
    CULTURE("文化"),
    ECOLOGY("生态"),
    LIVELIHOOD("民生小事"),
    GRASSROOTS_GOVERNANCE("基层治理");

    private final String value;

    ContentEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}