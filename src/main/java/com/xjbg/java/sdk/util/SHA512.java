package com.xjbg.java.sdk.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * @author kesc
 * @ClassName SHA512
 * @Description
 * @date 2017年10月28日 下午12:23:57
 */
public class SHA512 {
    private static volatile SHA512 instance = null;
    private final String ALGORITHM = "SHA-512";

    private SHA512() {
        super();
    }

    public static SHA512 getInstance() {
        if (instance == null) {
            synchronized (SHA512.class) {
                if (instance == null) {
                    instance = new SHA512();
                }
            }
        }
        return instance;
    }

    /**
     * @param src the original string to be digested
     * @return a newly digested base64-string
     * @throws NoSuchAlgorithmException
     * @Description sha-512 digest
     */
    public String SHA(final String src) throws NoSuchAlgorithmException {
        if (src == null) {
            return null;
        }
        MessageDigest messageDigest = MessageDigest.getInstance(ALGORITHM);
        messageDigest.update(src.getBytes());
        byte[] shaByte = messageDigest.digest();
        return Base64.getEncoder().encodeToString(shaByte);
    }

    public static void main(String[] args) throws NoSuchAlgorithmException {
        System.out.print(SHA512.getInstance().SHA("fds宿舍"));
    }
}
