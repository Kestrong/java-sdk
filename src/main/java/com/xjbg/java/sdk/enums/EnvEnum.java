package com.xjbg.java.sdk.enums;

import lombok.Getter;

/**
 * @author kesc
 * @since 2019/3/15
 */
@Getter
public enum EnvEnum {
    PRD("prd"), LOCAL("local"), DEV("dev"), TEST("test"), DEMO("demo");
    private String env;

    EnvEnum(String env) {
        this.env = env;
    }
}
