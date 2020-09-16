package com.xjbg.java.sdk.util;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * @author kesc
 * @since 2019/6/24
 */
public final class Base64Util {
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    public static byte[] encode(byte[] src) {
        return src == null || src.length == 0 ? src : Base64.getEncoder().encode(src);
    }

    public static String encodeToString(byte[] src) {
        return encodeToString(src, DEFAULT_CHARSET);
    }

    public static String encodeToString(byte[] src, Charset charset) {
        if (src == null) {
            return null;
        }
        if (src.length == 0) {
            return "";
        }
        return charset == null ? new String(encode(src)) : new String(encode(src), charset);
    }

    public static byte[] decode(byte[] src) {
        return src == null || src.length == 0 ? src : Base64.getDecoder().decode(src);
    }

    public static byte[] decodeFromString(String src) {
        return decodeFromString(src, DEFAULT_CHARSET);
    }

    public static byte[] decodeFromString(String src, Charset charset) {
        if (src == null) {
            return null;
        }
        if (src.isEmpty()) {
            return new byte[0];
        }
        return charset == null ? decode(src.getBytes()) : decode(src.getBytes(charset));
    }

    public static byte[] encodeUrlSafe(byte[] src) {
        return src == null || src.length == 0 ? src : Base64.getUrlEncoder().encode(src);
    }

    public static byte[] decodeUrlSafe(byte[] src) {
        return src == null || src.length == 0 ? src : Base64.getUrlDecoder().decode(src);
    }

    public static String encodeToUrlSafeString(byte[] src) {
        return encodeToUrlSafeString(src, DEFAULT_CHARSET);
    }

    public static String encodeToUrlSafeString(byte[] src, Charset charset) {
        if (charset == null) {
            return new String(encodeUrlSafe(src));
        }
        return new String(encodeUrlSafe(src), charset);
    }

    public static byte[] decodeFromUrlSafeString(String src) {
        if (src == null) {
            return null;
        }
        return decodeUrlSafe(src.getBytes(DEFAULT_CHARSET));
    }

    public static byte[] decodeFromUrlSafeString(String src, Charset charset) {
        if (src == null) {
            return null;
        }
        if (charset == null) {
            return decodeUrlSafe(src.getBytes());
        }
        return decodeUrlSafe(src.getBytes(charset));
    }
}
