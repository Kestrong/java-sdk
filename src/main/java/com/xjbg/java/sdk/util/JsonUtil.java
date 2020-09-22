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
        if (OBJECT_MAPPER == null) {
            synchronized (JsonUtil.class) {
                if (OBJECT_MAPPER == null) {
                    OBJECT_MAPPER = objectMapper;
                }
            }
        }
    }

    public static ObjectMapper getObjectMapper() {
        return OBJECT_MAPPER == null ? DEFAULT_OBJECT_MAPPER : OBJECT_MAPPER;
    }

    public static <T> String toJsonString(T source) {
        try {
            if (source instanceof String) {
                return (String) source;
            }
            return getObjectMapper().writeValueAsString(source);
        } catch (Exception var3) {
            throw new RuntimeException(var3);
        }
    }

    public static <T> T toObject(String source, Class<T> clazz) {
        try {
            if (clazz.isAssignableFrom(String.class)) {
                return (T) source;
            }
            return getObjectMapper().readValue(source, clazz);
        } catch (Exception var4) {
            throw new RuntimeException(var4);
        }
    }

    /**
     * deserialize string to object with generic type
     *
     * @param source         json string
     * @param clazz          object class
     * @param parametricType generic type
     * @return T<P>
     */
    public static <T, P> T toObject(String source, Class<T> clazz, Class<P> parametricType) {
        try {
            ObjectMapper objectMapper = getObjectMapper();
            JavaType javaType = objectMapper.getTypeFactory().constructParametricType(clazz, parametricType);
            return objectMapper.readValue(source, javaType);
        } catch (Exception var4) {
            throw new RuntimeException(var4);
        }
    }

    /**
     * deserialize string to object with list generic type
     *
     * @param source         json string
     * @param clazz          object class
     * @param parametricType generic type
     * @return like T<List<P>>
     */
    public static <T, P> T toObjectCollection(String source, Class<T> clazz, Class<P> parametricType) {
        try {
            ObjectMapper objectMapper = getObjectMapper();
            JavaType javaType = objectMapper.getTypeFactory().constructCollectionType(List.class, parametricType);
            JavaType type = objectMapper.getTypeFactory().constructParametricType(clazz, javaType);
            return objectMapper.readValue(source, type);
        } catch (Exception var4) {
            throw new RuntimeException(var4);
        }
    }


    public static <T> List<T> toList(String source, Class<T> tClass) {
        try {
            ObjectMapper objectMapper = getObjectMapper();
            JavaType javaType = objectMapper.getTypeFactory().constructCollectionLikeType(List.class, tClass);
            return objectMapper.readValue(source, javaType);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T, P> List<T> toList(String source, Class<T> valueType, Class<P> valueParametricType) {
        try {
            ObjectMapper objectMapper = getObjectMapper();
            JavaType elementType = objectMapper.getTypeFactory().constructParametricType(valueType, valueParametricType);
            JavaType listType = objectMapper.getTypeFactory().constructCollectionType(List.class, elementType);
            return objectMapper.readValue(source, listType);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <V> Map<String, V> toMap(String json, Class<V> valueClazz) {
        return toMap(json, String.class, valueClazz);
    }

    public static Map<Object, Object> toMap(String json) {
        return toMap(json, Object.class, Object.class);
    }

    public static <K, T> Map<K, T> toMap(String json, Class<K> keyType, Class<T> valueType) {
        try {
            ObjectMapper objectMapper = getObjectMapper();
            JavaType javaType = objectMapper.getTypeFactory().constructMapLikeType(Map.class, keyType, valueType);
            return objectMapper.readValue(json, javaType);
        } catch (IOException var5) {
            throw new RuntimeException(var5);
        }
    }
}
