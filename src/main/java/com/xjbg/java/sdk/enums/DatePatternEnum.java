package com.xjbg.java.sdk.enums;

/**
 * @author kesc
 * @since 2017/12/25
 */
public enum DatePatternEnum {
    YYYY("yyyy"),
    YYYYMM("yyyyMM"),
    YYYYMMDD("yyyyMMdd"),
    YYYYMMDDHH("yyyyMMddHH"),
    YYMMDDHHMMSS("yyMMddHHmmss"),
    YYYYMMDDHHMMSS("yyyyMMddHHmmss"),
    YYYYMMDDHHMM("yyyyMMddHHmm"),
    YYYYMMDDHHMMSSSSS("yyyyMMddHHmmssSSS"),
    YYYYMM_BYSEP("yyyy-MM"),
    YYYYMMDD_BYSEP("yyyy-MM-dd"),
    YYYYMMDDHH_BYSEP("yyyy-MM-dd HH"),
    MMDD_BYSEP("MM-dd"),
    YYYYMMDDHHMMSS_BYSEP("yyyy-MM-dd HH:mm:ss"),
    YYYYMMDDHHMM_BYSEP("yyyy-MM-dd HH:mm"),
    YYYYMMDDHHMMSSSSS_BYSEP("yyyy-MM-dd HH:mm:ss.SSS"),
    HHMMSS("HHmmss"),
    HHMM_BYSEP("HH:mm"),
    HHMMSS_BYSEP("HH:mm:ss"),
    YYYYMM_BYSLASH("yyyy/MM"),
    YYYYMMDD_BYSLASH("yyyy/MM/dd"),
    YYYYMMDDHH_BYSLASH("yyyy/MM/dd HH"),
    MMDD_BYSLASH("MM/dd"),
    YYYYMMDDHHMMSS_BYSLASH("yyyy/MM/dd HH:mm:ss"),
    YYYYMMDDHHMM_BYSLASH("yyyy/MM/dd HH:mm"),
    YYYYMMDDHHMMSSSSS_BYSLASH("yyyy/MM/dd HH:mm:ss.SSS"),
    MINYEAR("1900"),
    MAXYEAR("9999"),
    MINDATE("19000101"),
    MAXDATE("99991231"),
    MINDATE_BYSEP("1900-01-01"),
    MAXDATE_BYSEP("9999-12-31");

    private String format = null;

    private DatePatternEnum(String format) {
        this.format = format;
    }

    public String getFormat() {
        return format;
    }
}
