package com.xjbg.java.sdk.encrypt;

import com.xjbg.java.sdk.util.HexUtil;
import lombok.SneakyThrows;
import org.apache.commons.codec.digest.MessageDigestAlgorithms;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
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

    @SneakyThrows
    private MessageDigest getMessageDigest() {
        return MessageDigest.getInstance(MessageDigestAlgorithms.MD5);
    }

    public byte[] md5(final byte[] original) {
        if (original == null) {
            return null;
        }
        MessageDigest md = getMessageDigest();
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
        return HexUtil.bytes2HexLower(md5(original));
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

    public byte[] md5(final InputStream original) throws IOException {
        if (original == null) {
            return null;
        }
        final byte[] buffer = new byte[1024];
        MessageDigest digest = getMessageDigest();
        try (InputStream input = new BufferedInputStream(original)) {
            int read;
            while ((read = input.read(buffer)) != -1) {
                digest.update(buffer, 0, read);
            }
        }
        return digest.digest();
    }

    public String md5hex(final InputStream original) throws IOException {
        byte[] bytes = md5(original);
        if (bytes == null) {
            return null;
        }
        return HexUtil.bytes2HexLower(bytes);
    }

}
