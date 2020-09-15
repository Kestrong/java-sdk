package com.xjbg.java.sdk.util;

import com.xjbg.java.sdk.enums.Encoding;
import org.springframework.util.Base64Utils;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * @author kesc
 * @since 2019/9/11
 */
public class RsaOperator {
    private final String RSA = "RSA";
    private final int MAX_ENCRYPT_SIZE = 117;
    private final int MAX_DECRYPT_SIZE = 128;
    private static volatile RsaOperator instance = null;

    private RsaOperator() {
        super();
    }

    public static RsaOperator getInstance() {
        if (instance == null) {
            synchronized (RsaOperator.class) {
                if (instance == null) {
                    instance = new RsaOperator();
                }
            }
        }
        return instance;
    }

    public KeyPair genKey() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(RSA);
        keyPairGenerator.initialize(1024);
        return keyPairGenerator.generateKeyPair();
    }

    private byte[] multiSlot(byte[] input, Cipher cipher, int maxLength) throws IOException, BadPaddingException, IllegalBlockSizeException {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             ByteArrayInputStream in = new ByteArrayInputStream(input)) {
            byte[] buffer = new byte[maxLength];
            int read;
            while ((read = in.read(buffer, 0, maxLength)) != -1) {
                out.write(cipher.doFinal(buffer, 0, read));
            }
            return out.toByteArray();
        }
    }

    /**
     * 私钥加密
     *
     * @param data
     * @param key
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    public String encryptPrivate(final String data, final String key) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, IOException {
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(Base64Utils.decode(key.getBytes()));
        KeyFactory keyFactory = KeyFactory.getInstance(RSA);
        PrivateKey privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);
        Cipher cipher = Cipher.getInstance(RSA);
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
        byte[] result = multiSlot(data.getBytes(Encoding.UTF_8.getEncoding()), cipher, MAX_ENCRYPT_SIZE);
        return new String(Base64Utils.encode(result));
    }

    /**
     * 公钥解密
     *
     * @param data
     * @param key
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    public String decryptPublic(final String data, final String key) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, IOException {
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(Base64Utils.decode(key.getBytes()));
        KeyFactory keyFactory = KeyFactory.getInstance(RSA);
        PublicKey publicKey = keyFactory.generatePublic(x509EncodedKeySpec);
        Cipher cipher = Cipher.getInstance(RSA);
        cipher.init(Cipher.DECRYPT_MODE, publicKey);
        byte[] result = multiSlot(Base64Utils.decode(data.getBytes()), cipher, MAX_DECRYPT_SIZE);
        return new String(result, Encoding.UTF_8.getEncoding());
    }


    public String encryptPublic(final String data, final String key) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, IOException {
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(Base64Utils.decode(key.getBytes()));
        KeyFactory keyFactory = KeyFactory.getInstance(RSA);
        PublicKey publicKey = keyFactory.generatePublic(x509EncodedKeySpec);
        Cipher cipher = Cipher.getInstance(RSA);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] result = multiSlot(data.getBytes(Encoding.UTF_8.getEncoding()), cipher, MAX_ENCRYPT_SIZE);
        return new String(Base64Utils.encode(result));
    }

    public String decryptPrivate(final String data, final String key) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, IOException {
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(Base64Utils.decode(key.getBytes()));
        KeyFactory keyFactory = KeyFactory.getInstance(RSA);
        PrivateKey privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);
        Cipher cipher = Cipher.getInstance(RSA);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] result = multiSlot(Base64Utils.decode(data.getBytes()), cipher, MAX_DECRYPT_SIZE);
        return new String(result, Encoding.UTF_8.getEncoding());
    }

    public static void main(String[] args) throws UnsupportedEncodingException {
        String data = "{\"method\":\"RSA\",\"randCode\":\"zz13TnLnvybZZhJ3\",\"time\":\"2019-09-11 17:50:53\",\"equno\":\"djTest\",\"key\":\"MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCVcnve8fER4+hzQFLBplRGkzA078vJbRKMKBH5eUdQsKS+i3LEInYpNmKY+mTe+igjt9+slFqpKpg7zAWKveG1HIps7yJ8+1jetGyVD4ppVlFoRRJTgsGKf0kbsc8aw/O2i20e084y58IW7iaQ0rLfyCEwgWKQX6pH1GXL5jwBzwIDAQAB\"}";
        try {
            KeyPair keyPair = RsaOperator.getInstance().genKey();
            String publicKey = new String(Base64Utils.encode(keyPair.getPublic().getEncoded()));
            String privateKey = new String(Base64Utils.encode(keyPair.getPrivate().getEncoded()));
            String result = RsaOperator.getInstance().encryptPrivate(data, privateKey);
            System.out.println(result);
            String result2 = RsaOperator.getInstance().decryptPublic(result, publicKey);
            System.out.println(result2);
            String result3 = RsaOperator.getInstance().encryptPublic(data, publicKey);
            System.out.println(result3);
            String result4 = RsaOperator.getInstance().decryptPrivate(result3, privateKey);
            System.out.println(result4);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
