package com.xjbg.java.sdk.util;

import org.apache.commons.lang3.Validate;

import java.security.SecureRandom;

/**
 * @author kesc
 * @since 2019/6/24
 */
public final class RandomUtil {
    private static final SecureRandom RANDOM = new SecureRandom();

    public static String random(int count) {
        return random(count, false, false);
    }

    public static String randomAscii(int count) {
        return random(count, 32, 127, false, false);
    }

    public static String randomAlphabetic(int count) {
        return random(count, true, false);
    }

    public static String randomAlphanumeric(int count) {
        return random(count, true, true);
    }

    public static String randomNumeric(int count) {
        return random(count, false, true);
    }

    public static String random(int count, boolean letters, boolean numbers) {
        return random(count, 0, 0, letters, numbers);
    }

    public static String random(int count, int start, int end, boolean letters, boolean numbers) {
        return random(count, start, end, letters, numbers, null, RANDOM);
    }

    public static String random(int count, int start, int end, boolean letters, boolean numbers, char... chars) {
        return random(count, start, end, letters, numbers, chars, RANDOM);
    }

    public static String random(int count, int start, int end, boolean letters, boolean numbers, char[] chars, SecureRandom random) {
        if (count == 0) {
            return "";
        } else if (count < 0) {
            throw new IllegalArgumentException("Requested RANDOM string length " + count + " is less than 0.");
        } else if (chars != null && chars.length == 0) {
            throw new IllegalArgumentException("The chars array must not be empty");
        } else {
            if (start == 0 && end == 0) {
                if (chars != null) {
                    end = chars.length;
                } else if (!letters && !numbers) {
                    end = 2147483647;
                } else {
                    end = 123;
                    start = 32;
                }
            } else if (end <= start) {
                throw new IllegalArgumentException("Parameter end (" + end + ") must be greater than start (" + start + ")");
            }

            char[] buffer = new char[count];
            int gap = end - start;

            while (true) {
                while (true) {
                    while (count-- != 0) {
                        char ch;
                        if (chars == null) {
                            ch = (char) (random.nextInt(gap) + start);
                        } else {
                            ch = chars[random.nextInt(gap) + start];
                        }

                        if (letters && Character.isLetter(ch) || numbers && Character.isDigit(ch) || !letters && !numbers) {
                            if (ch >= '\udc00' && ch <= '\udfff') {
                                if (count == 0) {
                                    ++count;
                                } else {
                                    buffer[count] = ch;
                                    --count;
                                    buffer[count] = (char) ('\ud800' + random.nextInt(128));
                                }
                            } else if (ch >= '\ud800' && ch <= '\udb7f') {
                                if (count == 0) {
                                    ++count;
                                } else {
                                    buffer[count] = (char) ('\udc00' + random.nextInt(128));
                                    --count;
                                    buffer[count] = ch;
                                }
                            } else if (ch >= '\udb80' && ch <= '\udbff') {
                                ++count;
                            } else {
                                buffer[count] = ch;
                            }
                        } else {
                            ++count;
                        }
                    }

                    return new String(buffer);
                }
            }
        }
    }

    public static String random(int count, String chars) {
        return chars == null ? random(count, 0, 0, false, false, null, RANDOM) : random(count, chars.toCharArray());
    }

    public static String random(int count, char... chars) {
        return chars == null ? random(count, 0, 0, false, false, null, RANDOM) : random(count, 0, chars.length, false, false, chars, RANDOM);
    }

    public static byte[] nextBytes(int seed) {
        byte[] result = new byte[seed];
        RANDOM.nextBytes(result);
        return result;
    }

    public static int nextInt(int seed) {
        return RANDOM.nextInt(seed);
    }

    public static int nextInt(int seed, int base) {
        return RANDOM.nextInt(seed) + base;
    }

    public static long nextLong() {
        return RANDOM.nextLong();
    }

    public static float nextFloat() {
        return RANDOM.nextFloat();
    }

    public static double nextDouble() {
        return RANDOM.nextDouble();
    }

    public static double nextGaussian() {
        return RANDOM.nextGaussian();
    }

    public static long nextLong(long startInclusive, long endExclusive) {
        Validate.isTrue(endExclusive >= startInclusive, "Start value must be smaller or equal to end value.", new Object[0]);
        Validate.isTrue(startInclusive >= 0L, "Both range values must be non-negative.", new Object[0]);
        return startInclusive == endExclusive ? startInclusive : (long) nextDouble((double) startInclusive, (double) endExclusive);
    }

    public static double nextDouble(double startInclusive, double endInclusive) {
        Validate.isTrue(endInclusive >= startInclusive, "Start value must be smaller or equal to end value.", new Object[0]);
        Validate.isTrue(startInclusive >= 0.0D, "Both range values must be non-negative.", new Object[0]);
        return startInclusive == endInclusive ? startInclusive : startInclusive + (endInclusive - startInclusive) * RANDOM.nextDouble();
    }

    public static float nextFloat(float startInclusive, float endInclusive) {
        Validate.isTrue(endInclusive >= startInclusive, "Start value must be smaller or equal to end value.", new Object[0]);
        Validate.isTrue(startInclusive >= 0.0F, "Both range values must be non-negative.", new Object[0]);
        return startInclusive == endInclusive ? startInclusive : startInclusive + (endInclusive - startInclusive) * RANDOM.nextFloat();
    }
}
