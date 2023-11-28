package com.xjbg.java.sdk.encrypt;

import com.xjbg.java.sdk.util.RandomUtil;
import com.xjbg.java.sdk.util.StringUtil;
import lombok.SneakyThrows;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

/**
 * @author kesc
 * @date 2016年12月9日 上午9:00:36
 */
@SuppressWarnings({"unused"})
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
        return encrypt(StringUtil.getUTF8Bytes(src), key, ivParameter);
    }

    public String encrypt(final String src, final String key) {
        return encrypt(StringUtil.getUTF8Bytes(src), StringUtil.getBytes(key), ivParameter);
    }

    public String encrypt(final String src, final String key, final String iv) {
        return encrypt(StringUtil.getUTF8Bytes(src),
                StringUtil.getBytes(key),
                StringUtil.getBytes(iv));
    }

    public String encrypt(final byte[] src) {
        return encrypt(src, key, ivParameter);
    }

    public String encrypt(final byte[] src, final byte[] key) {
        return encrypt(src, key, ivParameter);
    }

    public String encrypt(final byte[] src, final byte[] key, final byte[] iv) {
        byte[] encrypted = encryptRaw(src, key, iv);
        return new String(Base64.getEncoder().encode(encrypted), StandardCharsets.UTF_8);
    }

    public String encrypt(final InputStream src) throws IOException {
        return encrypt(src, key, ivParameter);
    }

    public String encrypt(final InputStream src, final String key, final String iv) throws IOException {
        return encrypt(src, StringUtil.getBytes(key), StringUtil.getBytes(iv));
    }

    public String encrypt(final InputStream src, final byte[] key, final byte[] iv) throws IOException {
        byte[] encrypted = encryptRaw(src, key, iv);
        return new String(Base64.getEncoder().encode(encrypted), StandardCharsets.UTF_8);
    }

    public byte[] encryptRaw(final byte[] src) {
        return encryptRaw(src, key, ivParameter);
    }

    public byte[] encryptRaw(final String src) {
        return encryptRaw(StringUtil.getUTF8Bytes(src), key, ivParameter);
    }

    public byte[] encryptRaw(final String src, final String key, final String iv) {
        return encryptRaw(StringUtil.getUTF8Bytes(src),
                StringUtil.getBytes(key),
                StringUtil.getBytes(iv));
    }

    @SneakyThrows
    public byte[] encryptRaw(final byte[] src, final byte[] key, final byte[] iv) {
        checkSrc(src);
        Cipher cipher = getCipher(key, iv, Cipher.ENCRYPT_MODE);
        return cipher.doFinal(src);
    }

    public byte[] encryptRaw(final InputStream src) throws IOException {
        return encryptRaw(src, key, ivParameter);
    }

    public byte[] encryptRaw(final InputStream src, final String key, final String iv) throws IOException {
        return encryptRaw(src, StringUtil.getBytes(key), StringUtil.getBytes(iv));
    }

    public byte[] encryptRaw(final InputStream src, final byte[] key, final byte[] iv) throws IOException {
        try (InputStream input = new BufferedInputStream(src)) {
            Cipher cipher = getCipher(key, iv, Cipher.ENCRYPT_MODE);
            final byte[] buffer = new byte[1024];
            int read;
            while ((read = input.read(buffer)) != -1) {
                cipher.update(buffer, 0, read);
            }
            return cipher.doFinal();
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            throw new RuntimeException(e);
        }
    }

    public String decrypt(final String src) {
        return decrypt(StringUtil.getUTF8Bytes(src), key, ivParameter);
    }

    public String decrypt(final String src, final String key) {
        return decrypt(StringUtil.getUTF8Bytes(src), StringUtil.getBytes(key), ivParameter);
    }

    public String decrypt(final String src, final String key, final String iv) {
        return decrypt(StringUtil.getUTF8Bytes(src),
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
        return new String(decryptAsBytes(src, key, iv), StandardCharsets.UTF_8);
    }

    public byte[] decryptAsBytes(final byte[] src, final byte[] key, final byte[] iv) {
        checkSrc(src);
        // 先用base64解密
        byte[] decrypted = Base64.getDecoder().decode(src);
        return decryptRaw(decrypted, key, iv);
    }

    public byte[] decryptAsBytes(final String src) {
        return decryptAsBytes(StringUtil.getUTF8Bytes(src), key, ivParameter);
    }

    public byte[] decryptAsBytes(final byte[] src) {
        return decryptAsBytes(src, key, ivParameter);
    }

    public byte[] decryptAsBytes(final String src, final String key, final String iv) {
        return decryptAsBytes(StringUtil.getUTF8Bytes(src), StringUtil.getBytes(key), StringUtil.getBytes(iv));
    }

    public String decrypt(final InputStream src) throws IOException {
        return decrypt(src, key, ivParameter);
    }

    public String decrypt(final InputStream src, final String key, final String iv) throws IOException {
        return decrypt(src, StringUtil.getBytes(key), StringUtil.getBytes(iv));
    }

    public String decrypt(final InputStream src, final byte[] key, final byte[] iv) throws IOException {
        return new String(decryptAsBytes(src, key, iv), StandardCharsets.UTF_8);
    }

    public byte[] decryptAsBytes(final InputStream src) throws IOException {
        return decryptAsBytes(src, key, ivParameter);
    }

    public byte[] decryptAsBytes(final InputStream src, final String key, final String iv) throws IOException {
        return decryptAsBytes(src, StringUtil.getBytes(key), StringUtil.getBytes(iv));
    }

    public byte[] decryptAsBytes(final InputStream src, final byte[] key, final byte[] iv) throws IOException {
        try (InputStream input = new BufferedInputStream(src)) {
            Cipher cipher = getCipher(key, iv, Cipher.DECRYPT_MODE);
            final byte[] buffer = new byte[3 * 1024];
            int read;
            Base64.Decoder decoder = Base64.getDecoder();
            //需要先进行base64解码
            while ((read = input.read(buffer)) != -1) {
                if (read != buffer.length) {
                    byte[] copy = Arrays.copyOf(buffer, read);
                    cipher.update(decoder.decode(copy));
                } else {
                    cipher.update(decoder.decode(buffer));
                }
            }
            return cipher.doFinal();
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] decryptRaw(final byte[] src) {
        return decryptRaw(src, key, ivParameter);
    }

    public String decryptRawAsString(final byte[] src) {
        return decryptRawAsString(src, key, ivParameter);
    }

    public byte[] decryptRaw(final String src) {
        return decryptRaw(StringUtil.getUTF8Bytes(src), key, ivParameter);
    }

    public String decryptRawAsString(final String src) {
        return decryptRawAsString(StringUtil.getUTF8Bytes(src));
    }

    public byte[] decryptRaw(final String src, final String key, final String iv) {
        return decryptRaw(StringUtil.getUTF8Bytes(src),
                StringUtil.getBytes(key),
                StringUtil.getBytes(iv));
    }

    public String decryptRawAsString(final String src, final String key, final String iv) {
        return decryptRawAsString(StringUtil.getUTF8Bytes(src), StringUtil.getBytes(key), StringUtil.getBytes(iv));
    }

    @SneakyThrows
    public byte[] decryptRaw(final byte[] src, final byte[] key, final byte[] iv) {
        checkSrc(src);
        Cipher cipher = getCipher(key, iv, Cipher.DECRYPT_MODE);
        //不进行base64解码
        return cipher.doFinal(src);
    }

    public String decryptRawAsString(final byte[] src, final byte[] key, final byte[] iv) {
        return new String(decryptRaw(src, key, iv), StandardCharsets.UTF_8);
    }

    public byte[] decryptRaw(final InputStream src) throws IOException {
        return decryptRaw(src, key, ivParameter);
    }

    public String decryptRawAsString(final InputStream src) throws IOException {
        return decryptRawAsString(src, key, ivParameter);
    }

    public byte[] decryptRaw(final InputStream src, final String key, final String iv) throws IOException {
        return decryptRaw(src, StringUtil.getBytes(key), StringUtil.getBytes(iv));
    }

    public String decryptRawAsString(final InputStream src, final String key, final String iv) throws IOException {
        return decryptRawAsString(src, StringUtil.getBytes(key), StringUtil.getBytes(iv));
    }

    public byte[] decryptRaw(final InputStream src, final byte[] key, final byte[] iv) throws IOException {
        try (InputStream input = new BufferedInputStream(src)) {
            Cipher cipher = getCipher(key, iv, Cipher.DECRYPT_MODE);
            final byte[] buffer = new byte[1024];
            int read;
            while ((read = input.read(buffer)) != -1) {
                cipher.update(buffer, 0, read);
            }
            return cipher.doFinal();
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            throw new RuntimeException(e);
        }
    }

    public String decryptRawAsString(final InputStream src, final byte[] key, final byte[] iv) throws IOException {
        return new String(decryptRaw(src, key, iv), StandardCharsets.UTF_8);
    }

    private Cipher getCipher(final byte[] key, final byte[] iv, final int mode) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException {
        checkKey(key);
        checkIv(iv);
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        SecretKeySpec skeySpec = new SecretKeySpec(key, ALGORITHM);
        // 使用CBC模式，需要一个向量iv，可增加加密算法的强度
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
        cipher.init(mode, skeySpec, ivParameterSpec);
        return cipher;
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