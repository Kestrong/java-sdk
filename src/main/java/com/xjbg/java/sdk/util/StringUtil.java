package com.xjbg.java.sdk.util;


import org.apache.commons.lang3.StringUtils;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.UUID;


/**
 * @author kesc
 * @since 2017/12/25
 */
public final class StringUtil extends StringUtils {
    public static final String COMMA = ",";
    public static final String UNDER_LINE = "_";
    public static final String DOT = ".";
    public static final String MINUS = "-";
    public static final String VIRGULE = "/";
    public static final String COLON = ":";
    public static final String PLUS = "+";
    public static final String EQUIVALENT = "=";
    public static final String GT = ">";
    public static final String LT = "<";
    public static final String VERTICAL_LINE = "|";
    public static final String STAR = "*";
    public static final String PERCENT = "%";
    public static final String AND = "&";
    public static final String DOLOR = "$";
    public static final String L_QUOT = "(";
    public static final String R_QUOT = ")";
    public static final String SEMICOLON = ";";
    public static final String AT = "@";
    public static final String EXCLAMATION = "!";

    public static String uuid() {
        return UUID.randomUUID().toString();
    }

    public static String latterUuid() {
        return UUID.randomUUID().toString().replace(MINUS, EMPTY);
    }

    public static String camel2Underline(String str) {
        if (isBlank(str)) {
            return str;
        }
        str = str.trim();
        StringBuilder result = new StringBuilder(str.length());
        result.append(Character.toLowerCase(str.charAt(0)));
        for (int i = 1; i < str.length(); i++) {
            char ch = str.charAt(i);
            if (Character.isUpperCase(ch)) {
                result.append('_').append(Character.toLowerCase(ch));
            } else {
                result.append(ch);
            }
        }
        return result.toString();
    }

    public static String underline2Camel(String str) {
        if (isBlank(str)) {
            return str;
        }
        str = str.trim();
        StringBuilder result = new StringBuilder(str.length());
        boolean flag = false;
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            if ('_' == ch) {
                flag = true;
            } else {
                if (flag) {
                    result.append(Character.toUpperCase(ch));
                    flag = false;
                } else {
                    result.append(ch);
                }
            }
        }
        return result.toString();
    }

    public static byte[] getBytes(String src) {
        if (src == null) {
            return null;
        }
        return src.getBytes();
    }

    public static byte[] getBytes(String src, String charset) {
        return getBytes(src, Charset.forName(charset));
    }

    public static byte[] getBytes(String src, Charset charset) {
        if (src == null) {
            return null;
        }
        return src.getBytes(charset);
    }

    public static byte[] getUTF8Bytes(String src) {
        return getBytes(src, StandardCharsets.UTF_8);
    }

}
