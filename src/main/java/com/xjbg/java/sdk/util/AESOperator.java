package com.xjbg.java.sdk.util;

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
    /**
     * transformation - the name of the transformation, e.g.,
     * AES/CBC/PKCS5Padding.
     */
    private final String transformation = "AES/CBC/PKCS5Padding";
    /**
     * 加密用的Key 可以用26个字母和数字组成 此处使用AES-128-CBC加密模式，key需要为16位。
     */
    private String sKey;
    private String ivParameter;
    private final String algorithm = "AES";
    private final String charset = "UTF-8";

    public AESOperator() {
    }

    public String getsKey() {
        return sKey;
    }

    public void setsKey(String sKey) {
        this.sKey = sKey;
    }

    public String getIvParameter() {
        return ivParameter;
    }

    public void setIvParameter(String ivParameter) {
        this.ivParameter = ivParameter;
    }

    public String encrypt(final String sSrc) {
        return encrypt(sSrc, sKey);
    }

    public String encrypt(final String sSrc, final String key) {
        try {
            Cipher cipher = Cipher.getInstance(transformation);
            byte[] raw = key.getBytes(charset);
            SecretKeySpec skeySpec = new SecretKeySpec(raw, algorithm);
            // 使用CBC模式，需要一个向量iv，可增加加密算法的强度
            IvParameterSpec iv = new IvParameterSpec(ivParameter.getBytes(charset));
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
            byte[] encrypted = cipher.doFinal(sSrc.getBytes(charset));
            return new String(Base64.getEncoder().encode(encrypted), charset);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String decrypt(final String sSrc) {
        return decrypt(sSrc, sKey);
    }

    public String decrypt(final String sSrc, final String key) {
        try {
            byte[] raw = key.getBytes(charset);
            SecretKeySpec skeySpec = new SecretKeySpec(raw, algorithm);
            Cipher cipher = Cipher.getInstance(transformation);
            IvParameterSpec iv = new IvParameterSpec(ivParameter.getBytes(charset));
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            // 先用base64解密
            byte[] decrypted = Base64.getDecoder().decode(sSrc);
            byte[] original = cipher.doFinal(decrypted);
            return new String(original, charset);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        AESOperator aesOperator = new AESOperator();
        aesOperator.setsKey("1234567890abcdef");
        aesOperator.setIvParameter("fedcba0987654321");
        String cSrc = "";
        System.out.println(cSrc + " 长度为" + cSrc.length());
        long lStart = System.currentTimeMillis();
        String enString = aesOperator.encrypt(cSrc);
        System.out.println("加密后的字串是：" + enString + "长度为" + enString.length());
        long lUseTime = System.currentTimeMillis() - lStart;
        System.out.println("加密耗时：" + lUseTime + "毫秒");
        lStart = System.currentTimeMillis();
        String DeString = aesOperator.decrypt(cSrc);
        System.out.println("解密后的字串是：" + DeString);
        lUseTime = System.currentTimeMillis() - lStart;
        System.out.println("解密耗时：" + lUseTime + "毫秒");
    }

}