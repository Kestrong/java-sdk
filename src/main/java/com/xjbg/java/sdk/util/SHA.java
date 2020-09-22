package com.xjbg.java.sdk.util;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * @author kesc
 * @ClassName SHA512
 * @Description
 * @date 2017年10月28日 下午12:23:57
 */
public class SHA {
    private static volatile SHA instance = null;

    private SHA() {
        super();
    }

    public static SHA getInstance() {
        if (instance == null) {
            synchronized (SHA.class) {
                if (instance == null) {
                    instance = new SHA();
                }
            }
        }
        return instance;
    }

    public byte[] sha512(final byte[] src) {
        if (src == null) {
            return null;
        }
        return DigestUtils.sha512(src);
    }

    public String sha512Hex(final byte[] src) {
        if (src == null) {
            return null;
        }
        return DigestUtils.sha512Hex(src);
    }

    public byte[] sha512(final String src) {
        if (src == null) {
            return null;
        }
        return sha512(src.getBytes(StandardCharsets.UTF_8));
    }

    public String sha512Hex(final String src) {
        if (src == null) {
            return null;
        }
        return sha512Hex(src.getBytes(StandardCharsets.UTF_8));
    }

    public byte[] sha512(final InputStream src) throws IOException {
        if (src == null) {
            return null;
        }
        return DigestUtils.sha512(src);
    }

    public String sha512Hex(final InputStream src) throws IOException {
        if (src == null) {
            return null;
        }
        return DigestUtils.sha512Hex(src);
    }

    public byte[] sha384(final byte[] src) {
        if (src == null) {
            return null;
        }
        return DigestUtils.sha384(src);
    }

    public String sha384Hex(final byte[] src) {
        if (src == null) {
            return null;
        }
        return DigestUtils.sha384Hex(src);
    }

    public byte[] sha384(final String src) {
        if (src == null) {
            return null;
        }
        return sha384(src.getBytes(StandardCharsets.UTF_8));
    }

    public String sha384Hex(final String src) {
        if (src == null) {
            return null;
        }
        return sha384Hex(src.getBytes(StandardCharsets.UTF_8));
    }

    public byte[] sha384(final InputStream src) throws IOException {
        if (src == null) {
            return null;
        }
        return DigestUtils.sha384(src);
    }

    public String sha384Hex(final InputStream src) throws IOException {
        if (src == null) {
            return null;
        }
        return DigestUtils.sha384Hex(src);
    }

    public byte[] sha256(final byte[] src) {
        if (src == null) {
            return null;
        }
        return DigestUtils.sha256(src);
    }

    public String sha256Hex(final byte[] src) {
        if (src == null) {
            return null;
        }
        return DigestUtils.sha256Hex(src);
    }

    public byte[] sha256(final String src) {
        if (src == null) {
            return null;
        }
        return sha256(src.getBytes(StandardCharsets.UTF_8));
    }

    public String sha256Hex(final String src) {
        if (src == null) {
            return null;
        }
        return sha256Hex(src.getBytes(StandardCharsets.UTF_8));
    }

    public byte[] sha256(final InputStream src) throws IOException {
        if (src == null) {
            return null;
        }
        return DigestUtils.sha256(src);
    }

    public String sha256Hex(final InputStream src) throws IOException {
        if (src == null) {
            return null;
        }
        return DigestUtils.sha256Hex(src);
    }

    public byte[] sha1(final byte[] src) {
        if (src == null) {
            return null;
        }
        return DigestUtils.sha1(src);
    }

    public String sha1Hex(final byte[] src) {
        if (src == null) {
            return null;
        }
        return DigestUtils.sha1Hex(src);
    }

    public byte[] sha1(final String src) {
        if (src == null) {
            return null;
        }
        return sha1(src.getBytes(StandardCharsets.UTF_8));
    }

    public String sha1Hex(final String src) {
        if (src == null) {
            return null;
        }
        return sha1Hex(src.getBytes(StandardCharsets.UTF_8));
    }

    public byte[] sha1(final InputStream src) throws IOException {
        if (src == null) {
            return null;
        }
        return DigestUtils.sha1(src);
    }

    public String sha1Hex(final InputStream src) throws IOException {
        if (src == null) {
            return null;
        }
        return DigestUtils.sha1Hex(src);
    }

    public static void main(String[] args) {
        SHA sha = SHA.getInstance();
        String src = "fds宿舍";
        System.out.println(sha.sha1Hex(src));
        System.out.println(sha.sha256Hex(src));
        System.out.println(sha.sha384Hex(src));
        System.out.println(sha.sha512Hex(src));
    }
}
