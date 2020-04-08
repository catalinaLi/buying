package com.crawler.enums;

/**
 * @Author: lllx
 * @Description:
 * @Date: Created on 11:54 2020/4/8
 * @Modefied by:
 */
public enum TimingType {
    FIXED_RATE("固定频率"),
    FIXED_DELAY("固定延迟");
    private String desc;

    TimingType(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}
