package com.xjbg.java.sdk.util;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xjbg.java.sdk.customize.jackson.CustomObjectMapper;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author kesc
 * @since 2018/1/27
 */
@SuppressWarnings({"unchecked", "unused"})
public final class JsonUtil {
    private static final ObjectMapper DEFAULT_OBJECT_MAPPER = new CustomObjectMapper();
    private static volatile ObjectMapper OBJECT_MAPPER;

    public static void setObjectMapper(ObjectMapper objectMapper) {
        OBJECT_MAPPER = objectMapper;
    }

    private static ObjectMapper getObjectMapper() {
        return OBJECT_MAPPER == null ? DEFAULT_OBJECT_MAPPER : OBJECT_MAPPER;
    }

    private static <T> String toJsonString(T source, boolean pretty) {
        try {
            if (source == null) {
                return null;
            }
            if (source instanceof String) {
                return (String) source;
            }
            ObjectMapper objectMapper = getObjectMapper();
            if (pretty) {
                return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(source);
            }
            return objectMapper.writeValueAsString(source);
        } catch (Exception var3) {
            throw new RuntimeException(var3);
        }
    }

    public static <T> String toJsonString(T source) {
        return toJsonString(source, Boolean.FALSE);
    }

    public static <T> String toJsonStringPretty(T source) {
        return toJsonString(source, Boolean.TRUE);
    }

    @SuppressWarnings("unchecked")
    public static <T> T toObject(String source, Class<T> clazz) {
        try {
            if (source == null) {
                return null;
            }
            if (clazz.isAssignableFrom(String.class)) {
                return (T) source;
            }
            return getObjectMapper().readValue(source, clazz);
        } catch (Exception var4) {
            throw new RuntimeException(var4);
        }
    }

    public static <T> T toObject(String source, Class<T> clazz, Class<?>... parametricType) {
        try {
            if (source == null) {
                return null;
            }
            JavaType javaType = getObjectMapper().getTypeFactory().constructParametricType(clazz, parametricType);
            return getObjectMapper().readValue(source, javaType);
        } catch (Exception var4) {
            throw new RuntimeException(var4);
        }
    }

    public static <T> List<T> toList(String source, Class<T> tClass) {
        try {
            if (source == null) {
                return null;
            }
            JavaType javaType = getObjectMapper().getTypeFactory().constructCollectionLikeType(List.class, tClass);
            return getObjectMapper().readValue(source, javaType);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> List<T> toList(String source, Class<T> valueType, Class<?>... valueParametricType) {
        try {
            if (source == null) {
                return null;
            }
            JavaType elementType = getObjectMapper().getTypeFactory().constructParametricType(valueType, valueParametricType);
            JavaType listType = getObjectMapper().getTypeFactory().constructCollectionType(List.class, elementType);
            return getObjectMapper().readValue(source, listType);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <V> Map<String, V> toMap(String source, Class<V> valueClazz) {
        return toMap(source, String.class, valueClazz);
    }

    public static Map<Object, Object> toMap(String source) {
        return toMap(source, Object.class, Object.class);
    }

    public static <K, T> Map<K, T> toMap(String source, Class<K> keyType, Class<T> valueType) {
        try {
            if (source == null) {
                return null;
            }
            JavaType javaType = getObjectMapper().getTypeFactory().constructMapLikeType(Map.class, keyType, valueType);
            return getObjectMapper().readValue(source, javaType);
        } catch (IOException var5) {
            throw new RuntimeException(var5);
        }
    }

}
