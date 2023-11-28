package com.xjbg.java.sdk.encrypt;

import com.xjbg.java.sdk.enums.Encoding;
import com.xjbg.java.sdk.util.Base64Util;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.security.*;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * @author kesc
 * @since 2019/9/11
 */
public class RsaOperator {
    private final static String RSA = "RSA";
    private final static int DEFAULT_MAX_ENCRYPT_SIZE = 117;
    private final static int DEFAULT_MAX_DECRYPT_SIZE = 128;
    private int maxEncSize;
    private int maxDecSize;

    public RsaOperator() {
        this.maxDecSize = DEFAULT_MAX_DECRYPT_SIZE;
        this.maxEncSize = DEFAULT_MAX_ENCRYPT_SIZE;
    }

    public RsaOperator(int maxEncSize, int maxDecSize) {
        this.maxEncSize = maxEncSize;
        this.maxDecSize = maxDecSize;
    }

    public KeyPair genKey() throws NoSuchAlgorithmException {
        return genKey(1024);
    }

    public KeyPair genKey(int size) throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(RSA);
        keyPairGenerator.initialize(size);
        return keyPairGenerator.generateKeyPair();
    }

    private byte[] multiSlot(InputStream input, Cipher cipher, int maxLength) throws IOException, BadPaddingException, IllegalBlockSizeException {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             InputStream in = new BufferedInputStream(input)) {
            byte[] buffer = new byte[maxLength];
            int read;
            while ((read = in.read(buffer, 0, maxLength)) != -1) {
                out.write(cipher.doFinal(buffer, 0, read));
            }
            return out.toByteArray();
        }
    }

    private Cipher getCipher(byte[] encodedKey, int mode, boolean isPublic) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException {
        EncodedKeySpec encodedKeySpec = isPublic ? new X509EncodedKeySpec(encodedKey) : new PKCS8EncodedKeySpec(encodedKey);
        KeyFactory keyFactory = KeyFactory.getInstance(RSA);
        Key key = isPublic ? keyFactory.generatePublic(encodedKeySpec) : keyFactory.generatePrivate(encodedKeySpec);
        Cipher cipher = Cipher.getInstance(RSA);
        cipher.init(mode, key);
        return cipher;
    }

    private void checkKey(byte[] key) {
        if (key == null) {
            throw new IllegalArgumentException("key can not be null.");
        }
    }

    private void checkData(Object data) {
        if (data == null) {
            throw new IllegalArgumentException("data to be encrypted or decrypted can not be null.");
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
     * @throws IOException
     */
    public String encryptPrivate(final String data, final String key) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, IOException {
        byte[] dataRaw = data == null ? null : data.getBytes(Encoding.UTF_8.getEncoding());
        byte[] keyRaw = key == null ? null : Base64Util.decode(key.getBytes());
        return encryptPrivate(dataRaw, keyRaw);
    }

    public String encryptPrivate(final byte[] data, final byte[] key) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, IOException {
        checkData(data);
        return encryptPrivate(new ByteArrayInputStream(data), key);
    }

    public byte[] encryptPrivateAsBytes(final byte[] data, final byte[] key) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, IOException {
        checkData(data);
        return encryptPrivateAsBytes(new ByteArrayInputStream(data), key);
    }

    public String encryptPrivate(final InputStream data, final byte[] key) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, IOException {
        return new String(Base64Util.encode(encryptPrivateAsBytes(data, key)));
    }

    public byte[] encryptPrivateAsBytes(final InputStream data, final byte[] key) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, IOException {
        checkData(data);
        checkKey(key);
        Cipher cipher = getCipher(key, Cipher.ENCRYPT_MODE, Boolean.FALSE);
        return multiSlot(data, cipher, maxEncSize);
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
        byte[] dataRaw = data == null ? null : Base64Util.decode(data.getBytes());
        byte[] keyRaw = key == null ? null : Base64Util.decode(key.getBytes());
        return decryptPublic(dataRaw, keyRaw);
    }

    public String decryptPublic(final byte[] data, final byte[] key) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, IOException {
        checkData(data);
        return decryptPublic(new ByteArrayInputStream(data), key);
    }

    public byte[] decryptPublicAsBytes(final byte[] data, final byte[] key) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, IOException {
        checkData(data);
        return decryptPublicAsBytes(new ByteArrayInputStream(data), key);
    }

    public String decryptPublic(final InputStream data, final byte[] key) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, IOException {
        return new String(decryptPublicAsBytes(data, key), Encoding.UTF_8.getEncoding());
    }

    public byte[] decryptPublicAsBytes(final InputStream data, final byte[] key) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, IOException {
        checkData(data);
        checkKey(key);
        Cipher cipher = getCipher(key, Cipher.DECRYPT_MODE, Boolean.TRUE);
        return multiSlot(data, cipher, maxDecSize);
    }

    public String encryptPublic(final String data, final String key) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, IOException {
        byte[] dataRaw = data == null ? null : data.getBytes(Encoding.UTF_8.getEncoding());
        byte[] keyRaw = key == null ? null : Base64Util.decode(key.getBytes());
        return encryptPublic(dataRaw, keyRaw);
    }

    public String encryptPublic(final byte[] data, final byte[] key) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, IOException {
        checkData(data);
        return encryptPublic(new ByteArrayInputStream(data), key);
    }

    public byte[] encryptPublicAsBytes(final byte[] data, final byte[] key) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, IOException {
        checkData(data);
        return encryptPublicAsBytes(new ByteArrayInputStream(data), key);
    }

    public String encryptPublic(final InputStream data, final byte[] key) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, IOException {
        return new String(Base64Util.encode(encryptPublicAsBytes(data, key)));
    }

    public byte[] encryptPublicAsBytes(final InputStream data, final byte[] key) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, IOException {
        checkData(data);
        checkKey(key);
        Cipher cipher = getCipher(key, Cipher.ENCRYPT_MODE, Boolean.TRUE);
        return multiSlot(data, cipher, maxEncSize);
    }

    public String decryptPrivate(final String data, final String key) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, IOException {
        byte[] dataRaw = data == null ? null : Base64Util.decode(data.getBytes());
        byte[] keyRaw = key == null ? null : Base64Util.decode(key.getBytes());
        return decryptPrivate(dataRaw, keyRaw);
    }

    public String decryptPrivate(final byte[] data, final byte[] key) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, IOException {
        checkData(data);
        return decryptPrivate(new ByteArrayInputStream(data), key);
    }

    public byte[] decryptPrivateAsBytes(final byte[] data, final byte[] key) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, IOException {
        checkData(data);
        return decryptPrivateAsBytes(new ByteArrayInputStream(data), key);
    }

    public String decryptPrivate(final InputStream data, final byte[] key) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, IOException {
        return new String(decryptPrivateAsBytes(data, key), Encoding.UTF_8.getEncoding());
    }

    public byte[] decryptPrivateAsBytes(final InputStream data, final byte[] key) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, IOException {
        checkData(data);
        checkKey(key);
        Cipher cipher = getCipher(key, Cipher.DECRYPT_MODE, Boolean.FALSE);
        return multiSlot(data, cipher, maxDecSize);
    }

    public static void main(String[] args) {
        String data = "{\"method\":\"RSA\",\"randCode\":\"zz13TnLnvybZZhJ3\",\"time\":\"2019-09-11 17:50:53\",\"equno\":\"djTest\",\"key\":\"MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCVcnve8fER4+hzQFLBplRGkzA078vJbRKMKBH5eUdQsKS+i3LEInYpNmKY+mTe+igjt9+slFqpKpg7zAWKveG1HIps7yJ8+1jetGyVD4ppVlFoRRJTgsGKf0kbsc8aw/O2i20e084y58IW7iaQ0rLfyCEwgWKQX6pH1GXL5jwBzwIDAQAB\"}";
        try {
            RsaOperator rsaOperator = new RsaOperator();
            KeyPair keyPair = rsaOperator.genKey();
            String publicKey = new String(Base64Util.encode(keyPair.getPublic().getEncoded()));
            String privateKey = new String(Base64Util.encode(keyPair.getPrivate().getEncoded()));
            System.out.println("publicKey:" + publicKey + "\n" + "privateKey:" + privateKey);
            String result = rsaOperator.encryptPrivate(data, privateKey);
            System.out.println(result);
            String result2 = rsaOperator.decryptPublic(result, publicKey);
            System.out.println(result2);
            String result3 = rsaOperator.encryptPublic(data, publicKey);
            System.out.println(result3);
            String result4 = rsaOperator.decryptPrivate(result3, privateKey);
            System.out.println(result4);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
