package com.xjbg.java.sdk.util;

import org.apache.commons.lang3.math.NumberUtils;

/**
 * @author kesc
 * @since 2023-11-06 17:23
 */
public class NumberUtil extends NumberUtils {

    public static Integer toInteger(String s) {
        return toInteger(s, null);
    }

    public static Integer toInteger(String str, Integer defaultValue) {
        if (StringUtil.isBlank(str)) {
            return defaultValue;
        }
        try {
            return Integer.valueOf(str);
        } catch (final NumberFormatException nfe) {
            return defaultValue;
        }
    }

    public static Long toLongWrap(String s) {
        return toLongWrap(s, null);
    }

    public static Long toLongWrap(String str, Long defaultValue) {
        if (StringUtil.isBlank(str)) {
            return defaultValue;
        }
        try {
            return Long.valueOf(str);
        } catch (final NumberFormatException nfe) {
            return defaultValue;
        }
    }

    public static Float toFloatWrap(String s) {
        return toFloatWrap(s, null);
    }

    public static Float toFloatWrap(String str, Float defaultValue) {
        if (StringUtil.isBlank(str)) {
            return defaultValue;
        }
        try {
            return Float.valueOf(str);
        } catch (final NumberFormatException nfe) {
            return defaultValue;
        }
    }

    public static Double toDoubleWrap(String s) {
        return toDoubleWrap(s, null);
    }

    public static Double toDoubleWrap(String str, Double defaultValue) {
        if (StringUtil.isBlank(str)) {
            return defaultValue;
        }
        try {
            return Double.valueOf(str);
        } catch (final NumberFormatException nfe) {
            return defaultValue;
        }
    }

}
