package com.xjbg.java.sdk.util;

import com.xjbg.java.sdk.enums.Encoding;
import lombok.SneakyThrows;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

/**
 * @author kesc
 * @ClassName AESOperator
 * @Description AES加密
 * @date 2016年12月9日 上午9:00:36
 */
public class AESOperator {
    private final static String ALGORITHM = "AES";
    private final static int KEY_LENGTH = 16;
    /**
     * transformation - the name of the transformation, e.g.,
     * AES/CBC/PKCS5Padding.
     */
    private final static String TRANSFORMATION = "AES/CBC/PKCS5Padding";
    /**
     * 加密用的Key 可以用26个字母和数字组成 此处使用AES-128-CBC加密模式，key需要为16位。
     */
    private byte[] key;
    private byte[] ivParameter;

    public AESOperator() {
        this(RandomUtil.randomAlphanumeric(KEY_LENGTH), RandomUtil.randomAlphanumeric(KEY_LENGTH));
    }

    public AESOperator(String iv) {
        this(StringUtil.getBytes(iv));
    }

    public AESOperator(String key, String iv) {
        this(StringUtil.getBytes(key), StringUtil.getBytes(iv));
    }

    public AESOperator(byte[] iv) {
        checkIv(iv);
        this.ivParameter = iv;
    }

    public AESOperator(byte[] key, byte[] iv) {
        this(iv);
        checkKey(key);
        this.key = key;
    }

    private void checkIv(byte[] iv) {
        if (iv == null || iv.length != KEY_LENGTH) {
            throw new IllegalArgumentException(String.format("iv length must be %d.", KEY_LENGTH));
        }
    }

    private void checkKey(byte[] key) {
        if (key == null || key.length != KEY_LENGTH) {
            throw new IllegalArgumentException(String.format("key length must be %d.", KEY_LENGTH));
        }
    }

    private void checkSrc(byte[] src) {
        if (src == null) {
            throw new IllegalArgumentException("source to be encrypted or decrypted can not be null.");
        }
    }

    public String encrypt(final String src) {
        return encrypt(StringUtil.getBytes(src, Encoding.UTF_8.getEncoding()), key, ivParameter);
    }

    public String encrypt(final String src, final String key) {
        return encrypt(StringUtil.getBytes(src, Encoding.UTF_8.getEncoding()), StringUtil.getBytes(key), ivParameter);
    }

    public String encrypt(final String src, final String key, final String iv) {
        return encrypt(StringUtil.getBytes(src, Encoding.UTF_8.getEncoding()),
                StringUtil.getBytes(key),
                StringUtil.getBytes(iv));
    }

    public String encrypt(final byte[] src) {
        return encrypt(src, key, ivParameter);
    }

    public String encrypt(final byte[] src, final byte[] key) {
        return encrypt(src, key, ivParameter);
    }

    @SneakyThrows
    public String encrypt(final byte[] src, final byte[] key, final byte[] iv) {
        checkSrc(src);
        checkKey(key);
        checkIv(iv);
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        SecretKeySpec skeySpec = new SecretKeySpec(key, ALGORITHM);
        // 使用CBC模式，需要一个向量iv，可增加加密算法的强度
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, ivParameterSpec);
        byte[] encrypted = cipher.doFinal(src);
        return new String(Base64.getEncoder().encode(encrypted), Encoding.UTF_8.getEncoding());
    }

    public String decrypt(final String src) {
        return decrypt(StringUtil.getBytes(src, Encoding.UTF_8.getEncoding()), key, ivParameter);
    }

    public String decrypt(final String src, final String key) {
        return decrypt(StringUtil.getBytes(src, Encoding.UTF_8.getEncoding()), StringUtil.getBytes(key), ivParameter);
    }

    public String decrypt(final String src, final String key, final String iv) {
        return decrypt(StringUtil.getBytes(src, Encoding.UTF_8.getEncoding()),
                StringUtil.getBytes(key),
                StringUtil.getBytes(iv));
    }

    public String decrypt(final byte[] src) {
        return decrypt(src, key, ivParameter);
    }

    public String decrypt(final byte[] src, final byte[] key) {
        return decrypt(src, key, ivParameter);
    }

    @SneakyThrows
    public String decrypt(final byte[] src, final byte[] key, final byte[] iv) {
        checkSrc(src);
        checkKey(key);
        checkIv(iv);
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, ALGORITHM);
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
        // 先用base64解密
        byte[] decrypted = Base64.getDecoder().decode(src);
        byte[] original = cipher.doFinal(decrypted);
        return new String(original, Encoding.UTF_8.getEncoding());
    }

    public static void main(String[] args) {
        String key = "1234567890abcdef";
        String iv = "fedcba0987654321";
        AESOperator aesOperator = new AESOperator(key, iv);
        String cSrc = "test";
        System.out.println(cSrc + "长度为" + cSrc.length());
        long lStart = System.currentTimeMillis();
        String enString = aesOperator.encrypt(cSrc);
        System.out.println("加密后的字串是：" + enString + "长度为" + enString.length());
        long lUseTime = System.currentTimeMillis() - lStart;
        System.out.println("加密耗时：" + lUseTime + "毫秒");
        lStart = System.currentTimeMillis();
        String deString = aesOperator.decrypt(enString);
        System.out.println("还原后的字串是：" + deString);
        try {
            String deString2 = aesOperator.decrypt(cSrc);
            System.out.println("解密后的字串是：" + deString2);
        } catch (Exception e) {
            //ignore
        }
        lUseTime = System.currentTimeMillis() - lStart;
        System.out.println("解密耗时：" + lUseTime + "毫秒");
    }

}