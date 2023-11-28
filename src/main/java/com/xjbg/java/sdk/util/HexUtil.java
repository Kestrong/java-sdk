package com.xjbg.java.sdk.util;

import java.nio.charset.StandardCharsets;

/**
 * @author kesc
 * @since 2023-06-29 15:27
 */
public class HexUtil {
    private static final char[] HEX_DIGITS_LOWER = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    private static final char[] HEX_DIGITS_UPPER = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    private static String bytes2Hex(byte[] input, char[] HEX_DIGITS) {
        char[] str = new char[16 * 2];
        byte temp;
        int k = 0;
        for (int i = 0; i < 16; i++) {
            temp = input[i];
            str[k++] = HEX_DIGITS[temp >>> 4 & 0xf];
            str[k++] = HEX_DIGITS[temp & 0xf];
        }
        return new String(str);
    }

    public static String bytes2HexUpper(byte[] input) {
        return bytes2Hex(input, HEX_DIGITS_UPPER);
    }

    public static String bytes2HexLower(byte[] input) {
        return bytes2Hex(input, HEX_DIGITS_LOWER);
    }

    public static byte[] hex2Bytes(String input) {
        return hex2Bytes(input.getBytes(StandardCharsets.ISO_8859_1));
    }

    public static byte[] hex2Bytes(byte[] bytes) {
        int iLen = bytes.length;
        byte[] hexBytes = new byte[iLen / 2];
        for (int i = 0; i < iLen; i = i + 2) {
            String strTmp = new String(bytes, i, 2, StandardCharsets.ISO_8859_1);
            hexBytes[i / 2] = (byte) Integer.parseInt(strTmp, 16);
        }
        return hexBytes;
    }

}
