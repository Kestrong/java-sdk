package com.xjbg.java.sdk.util;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.Base64.Decoder;

/**
 * @author kesc
 * @ClassName Authenticator
 * @Description
 * @date 2017年7月18日 下午2:26:56
 */
public class Authenticator {
    private final SecureRandom secureRandom = new SecureRandom();
    public static final String PBKDF2_ALGORITHM = "PBKDF2WithHmacSHA512";
    public static final int SALT_BYTES = 24;
    public static final int HASH_BYTES = 24;
    public static final int PBKDF2_ITERATIONS = 1000;
    public static final String CHARSET = "UTF-8";

    public boolean validate(String givenPassword, String hash, String salt) {
        Decoder decoder = Base64.getDecoder();
        try {
            byte[] hashByte = decoder.decode(hash);
            byte[] saltByte = decoder.decode(salt);
            byte[] testHash = pbkdf2(givenPassword.toCharArray(), saltByte, PBKDF2_ITERATIONS, hashByte.length);
            return slowEquals(hashByte, testHash);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            return false;
        }
    }

    public boolean validate(char[] givenPassword, String hash, String salt) {
        Decoder decoder = Base64.getDecoder();
        try {
            byte[] hashByte = decoder.decode(hash);
            byte[] saltByte = decoder.decode(salt);
            byte[] testHash = pbkdf2(givenPassword, saltByte, PBKDF2_ITERATIONS, hashByte.length);
            return slowEquals(hashByte, testHash);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            return false;
        }
    }

    /**
     * Compares two byte arrays in length-constant time. This comparison method
     * is used so that password hashes cannot be extracted from an on-line
     * system using a timing attack and then attacked off-line.
     *
     * @param a the first byte array
     * @param b the second byte array
     * @return true if both byte arrays are the same, false if not
     */
    private boolean slowEquals(byte[] a, byte[] b) {
        int diff = a.length ^ b.length;
        for (int i = 0; i < a.length && i < b.length; i++) {
            diff |= a[i] ^ b[i];
        }
        return diff == 0;
    }

    /**
     * Computes the PBKDF2 hash of a password.
     *
     * @param password   the password to hash.
     * @param salt       the salt
     * @param iterations the iteration count (slowness factor)
     * @param bytes      the length of the hash to compute in bytes
     * @return the PBDKF2 hash of the password
     */
    private byte[] pbkdf2(char[] password, byte[] salt, int iterations, int bytes) throws NoSuchAlgorithmException, InvalidKeySpecException {
        PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, bytes * 8);
        SecretKeyFactory skf = SecretKeyFactory.getInstance(PBKDF2_ALGORITHM);
        return skf.generateSecret(spec).getEncoded();
    }

    public byte[] createHash(String password, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] hash = pbkdf2(password.toCharArray(), salt, PBKDF2_ITERATIONS, HASH_BYTES);
        return hash;
    }

    public byte[] createSalt() {
        byte[] salt = new byte[32];
        secureRandom.nextBytes(salt);
        return salt;
    }

    /**
     * @param src
     * @return
     * @throws UnsupportedEncodingException
     * @Description first encode byte to base64 encoded byte,second convert to
     * string with default charset
     */
    public String byteToBase64String(byte[] src) throws UnsupportedEncodingException {
        return new String(Base64.getEncoder().encode(src), CHARSET);
    }

    public static void main(String[] arg) throws NoSuchAlgorithmException, InvalidKeySpecException, UnsupportedEncodingException {
        Authenticator a = new Authenticator();
        byte[] salt = a.createSalt();
        byte[] hash = a.createHash("123456", salt);
        boolean b = a.validate("123456", a.byteToBase64String(hash), a.byteToBase64String(salt));
        System.out.println(b);
        System.out.println(a.byteToBase64String(hash));
        System.out.println(a.byteToBase64String(salt));
    }
}
