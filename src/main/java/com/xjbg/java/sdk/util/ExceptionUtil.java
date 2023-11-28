package com.xjbg.java.sdk.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

/**
 * @author kesc
 * @since 2023-04-23 16:44
 */
public class ExceptionUtil {

    public static String getTraceInfo(Throwable t) {
        if (t == null) {
            return null;
        }
        try (Writer writer = new StringWriter(); PrintWriter printWriter = new PrintWriter(writer)) {
            t.printStackTrace(printWriter);
            return writer.toString();
        } catch (Exception e) {
            return t.getMessage();
        }
    }

}
