package com.xjbg.java.sdk.enums;

import lombok.Getter;

/**
 * @author kesc
 * @date 2020-07-02 10:48
 */
@Getter
public enum FlagEnum {
    YES(1, "yes", "是"),
    NO(0, "no", "否"),

    SUCCESS(1, "success", "成功"),
    FAIL(0, "fail", "失败"),

    TRUE(1, "true", "正确"),
    FALSE(0, "false", "错误"),

    DELETED(0, "deleted", "已删除"),
    EXISTED(1, "existed", "未删除"),

    ENABLE(1, "enable", "启用"),
    DISABLE(0, "disable", "禁用"),

    STOPPED(0, "stopped", "停止"),
    RUNNING(1, "running", "运行中");

    private int flag;
    private String text;
    private String desc;

    FlagEnum(int flag, String text, String desc) {
        this.flag = flag;
        this.text = text;
        this.desc = desc;
    }
}
