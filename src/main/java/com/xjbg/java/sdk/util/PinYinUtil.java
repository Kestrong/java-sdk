package com.xjbg.java.sdk.util;

import lombok.extern.slf4j.Slf4j;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

/**
 * @author kesc
 * @since 2023-11-06 10:48
 */
@Slf4j
@SuppressWarnings("unused")
public class PinYinUtil {

    public static String getPinYin(String inputString) throws BadHanyuPinyinOutputFormatCombination {
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        format.setVCharType(HanyuPinyinVCharType.WITH_V);

        char[] input = inputString.trim().toCharArray();
        StringBuilder output = new StringBuilder();

        for (char anInput : input) {
            if (Character.toString(anInput).matches("[\\u4E00-\\u9FA5]+")) {
                String[] temp = PinyinHelper.toHanyuPinyinStringArray(anInput, format);
                output.append(temp[0]);
            } else {
                output.append(anInput);
            }
        }
        return output.toString();
    }

    public static String getFirstSpell(String chinese) throws BadHanyuPinyinOutputFormatCombination {
        StringBuilder sb = new StringBuilder();
        char[] arr = chinese.toCharArray();
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.UPPERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        for (char anArr : arr) {
            if (anArr > 128) {
                String[] temp = PinyinHelper.toHanyuPinyinStringArray(anArr, defaultFormat);
                if (temp != null && temp.length > 0) {
                    sb.append(temp[0].charAt(0));
                }
            } else {
                sb.append(anArr);
            }
        }
        return sb.toString().replaceAll("\\W", "").trim();
    }

    public static String getFullSpell(String chinese) throws BadHanyuPinyinOutputFormatCombination {
        StringBuilder sb = new StringBuilder();
        char[] arr = chinese.toCharArray();
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        for (char anArr : arr) {
            if (anArr > 128) {
                sb.append(PinyinHelper.toHanyuPinyinStringArray(anArr, defaultFormat)[0]);
            } else {
                sb.append(anArr);
            }
        }
        return sb.toString();
    }

}
