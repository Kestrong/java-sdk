package com.xjbg.java.sdk.util;

import com.xjbg.java.sdk.enums.Encoding;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author keshicong
 * @ClassName MD5
 * @Description
 * @mail 492167585@qq.com
 * @date 2016年10月1日 下午2:58:49
 */
public class MD5 {
    private static volatile MD5 instance = null;
    private final String charset = Encoding.UTF_8.getEncoding();

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

    public String getMD5(final byte[] original) throws NoSuchAlgorithmException {
        if (original == null) {
            return null;
        }
        // 用来将字节转换成 16 进制表示的字符
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(original);
        // MD5 的计算结果是一个 128 位的长整数 用字节表示就是 16 个字节
        byte tmp[] = md.digest();
        char str[] = new char[16 * 2];
        byte temp;
        int k = 0;
        for (int i = 0; i < 16; i++) {
            temp = tmp[i];
            // 取字节中高 4 位的数字转换 >>>为逻辑右移，将符号位一起右移
            str[k++] = hexDigits[temp >>> 4 & 0xf];
            // 取字节中低 4位的数字转换
            str[k++] = hexDigits[temp & 0xf];
        }
        return new String(str);
    }

    /**
     * @param original
     * @return
     * @throws UnsupportedEncodingException
     * @throws NoSuchAlgorithmException
     * @Description get md5 of input string
     */
    public String getMD5(final String original, final String charset) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        if (original == null) {
            return null;
        }
        return getMD5(original.getBytes(charset));
    }

    /**
     * @param original
     * @return
     * @throws UnsupportedEncodingException
     * @throws NoSuchAlgorithmException
     * @Description get md5 of input string
     */
    public String getMD5(final String original) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        if (original == null) {
            return null;
        }
        return getMD5(original.getBytes(charset));
    }
}
