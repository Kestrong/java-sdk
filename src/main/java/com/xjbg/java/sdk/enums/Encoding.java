package com.xjbg.java.sdk.enums;

import lombok.Getter;

/**
 * @author kesc
 * @since 2019/3/1
 */
@Getter
public enum Encoding {
    UTF_8("UTF-8"),
    GBK("GBK"),
    ISO_8859_1("ISO-8859-1"),;
    private String encoding;

    Encoding(String encoding) {
        this.encoding = encoding;
    }
}
