package com.xjbg.java.sdk.util;

import lombok.SneakyThrows;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * @author keshicong
 * @ClassName MD5
 * @Description
 * @date 2016年10月1日 下午2:58:49
 */
public class MD5 {
    /**
     * 用来将字节转换成 16 进制表示的字符
     */
    private static final char[] HEX_DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    private static volatile MD5 instance = null;

    private MD5() {
        super();
    }

    public static MD5 getInstance() {
        if (instance == null) {
            synchronized (MD5.class) {
                if (instance == null) {
                    instance = new MD5();
                }
            }
        }
        return instance;
    }

    private String toHex(byte[] input) {
        char[] str = new char[16 * 2];
        byte temp;
        int k = 0;
        for (int i = 0; i < 16; i++) {
            temp = input[i];
            // 取字节中高 4 位的数字转换 >>>为逻辑右移，将符号位一起右移
            str[k++] = HEX_DIGITS[temp >>> 4 & 0xf];
            // 取字节中低 4位的数字转换
            str[k++] = HEX_DIGITS[temp & 0xf];
        }
        return new String(str);
    }

    @SneakyThrows
    public byte[] md5(final byte[] original) {
        if (original == null) {
            return null;
        }
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(original);
        // MD5 的计算结果是一个 128 位的长整数 用字节表示就是 16 个字节
        return md.digest();
    }

    public byte[] md5(final String original, final Charset charset) {
        if (original == null) {
            return null;
        }
        return md5(original.getBytes(charset));
    }

    public byte[] md5(final String original) {
        if (original == null) {
            return null;
        }
        return md5(original.getBytes(StandardCharsets.UTF_8));
    }

    public String md5hex(final byte[] original) {
        return toHex(md5(original));
    }


    public String md5hex(final String original, final Charset charset) {
        if (original == null) {
            return null;
        }
        return md5hex(original.getBytes(charset));
    }

    public String md5hex(final String original) {
        if (original == null) {
            return null;
        }
        return md5hex(original.getBytes(StandardCharsets.UTF_8));
    }
}
