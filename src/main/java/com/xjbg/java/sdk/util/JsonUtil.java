package com.xjbg.java.sdk.util;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xjbg.java.sdk.customize.jackson.CustomObjectMapper;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

/**
 * @author kesc
 * @since 2018/1/27
 */
public final class JsonUtil {
    private static ObjectMapper objectMapper = new CustomObjectMapper();

    static {
        objectMapper.setTimeZone(TimeZone.getTimeZone("GMT+8"));
    }

    public static <T> String toJsonString(T source) {
        try {
            if (source instanceof String) {
                return (String) source;
            }
            return objectMapper.writeValueAsString(source);
        } catch (Exception var3) {
            throw new RuntimeException(var3);
        }
    }

    public static <T> T toObject(String source, Class<T> clazz) {
        try {
            if (clazz.isAssignableFrom(String.class)) {
                return (T) source;
            }
            return objectMapper.readValue(source, clazz);
        } catch (Exception var4) {
            throw new RuntimeException(var4);
        }
    }

    public static <T, P> T toObject(String source, Class<T> clazz, Class<P> parametricType) {
        try {
            JavaType javaType = objectMapper.getTypeFactory().constructParametricType(clazz, parametricType);
            return objectMapper.readValue(source, javaType);
        } catch (Exception var4) {
            throw new RuntimeException(var4);
        }
    }

    public static <T, P> T toObjectCollection(String source, Class<T> clazz, Class<P> parametricType) {
        try {
            JavaType javaType = objectMapper.getTypeFactory().constructCollectionType(List.class, parametricType);
            JavaType type = objectMapper.getTypeFactory().constructParametricType(clazz, javaType);
            return objectMapper.readValue(source, type);
        } catch (Exception var4) {
            throw new RuntimeException(var4);
        }
    }

    public static <T> List<T> toList(String source, Class<T> tClass) {
        try {
            JavaType javaType = objectMapper.getTypeFactory().constructParametricType(List.class, new Class[]{tClass});
            return objectMapper.readValue(source, javaType);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <K, T> Map<K, T> toMap(String json, Class<K> keyType, Class<T> valueType) {
        try {
            JavaType javaType = objectMapper.getTypeFactory().constructParametricType(Map.class, keyType, valueType);
            return objectMapper.readValue(json, javaType);
        } catch (IOException var5) {
            throw new RuntimeException(var5);
        }
    }

}
