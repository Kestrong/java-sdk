package com.xjbg.java.sdk.util;

/**
 * @author kesc
 * @ClassName Levenshtein
 * @Description 字符串匹配算法
 * @date 2017年5月31日 上午11:58:04
 */
public class Levenshtein {
    private static volatile Levenshtein instance = null;

    private Levenshtein() {
        super();
    }

    public static Levenshtein getInstance() {
        if (instance == null) {
            synchronized (Levenshtein.class) {
                if (instance == null) {
                    instance = new Levenshtein();
                }
            }
        }
        return instance;
    }

    public double compare(final String s1, final String s2) {
        double retval;
        int n = s1.length();
        int m = s2.length();
        if (0 == n) {
            retval = m;
        } else if (0 == m) {
            retval = n;
        } else {
            retval = 1.0 - (compare(s1, n, s2, m) / (Math.max(n, m)));
        }
        return retval;
    }

    private double compare(final String s1, final int n, final String s2, final int m) {
        int matrix[][] = new int[n + 1][m + 1];
        for (int i = 0; i <= n; i++) {
            matrix[i][0] = i;
        }
        for (int i = 0; i <= m; i++) {
            matrix[0][i] = i;
        }
        for (int i = 1; i <= n; i++) {
            int s1i = s1.codePointAt(i - 1);
            for (int j = 1; j <= m; j++) {
                int s2j = s2.codePointAt(j - 1);
                final int cost = s1i == s2j ? 0 : 1;
                matrix[i][j] = min3(matrix[i - 1][j] + 1, matrix[i][j - 1] + 1, matrix[i - 1][j - 1] + cost);
            }
        }
        return matrix[n][m];
    }

    private int min3(final int a, final int b, final int c) {
        return Math.min(Math.min(a, b), c);
    }

    public static void main(String[] args) {
        String a = "85°C";
        String b = "85°C";
        System.out.println(Levenshtein.getInstance().compare(a, b));
    }
}
