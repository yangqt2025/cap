package com.yupi.springbootinit.model.enums;



public enum DistrictEnum {
    PROVINCIAL("副省级"),
    MUNICIPAL("地市级"),
    ADMINISTRATIVE("行政执法");

    private final String value;

    DistrictEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}