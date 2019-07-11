package com.xjbg.java.sdk.util;

import com.esotericsoftware.reflectasm.MethodAccess;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nonnull;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

/**
 * @author kesc
 * @since 2019/6/24
 */
@Slf4j
public final class ReflectionUtil {
    /**
     * could not be modify
     */
    private final static LoadingCache<Class<?>, Field[]> CACHE_FIELD = CacheBuilder.newBuilder().softValues().maximumSize(100)
            .expireAfterWrite(1, TimeUnit.DAYS).build(new CacheLoader<Class<?>, Field[]>() {
                @Override
                public Field[] load(@Nonnull Class<?> clazz) throws Exception {
                    return clazz.getDeclaredFields();
                }
            });
    /**
     * could not be modify
     */
    private final static LoadingCache<Class<?>, MethodAccess> CACHE_METHOD = CacheBuilder.newBuilder().softValues().maximumSize(100)
            .expireAfterWrite(1, TimeUnit.DAYS).build(new CacheLoader<Class<?>, MethodAccess>() {
                @Override
                public MethodAccess load(@Nonnull Class<?> clazz) throws Exception {
                    return MethodAccess.get(clazz);
                }
            });

    public static <T> Field[] getField(Class<T> clazz) {
        try {
            return CACHE_FIELD.getUnchecked(clazz);
        } catch (Exception e) {
            return null;
        }
    }

    public static <T> List<Field> getAllField(Class<T> tClass) {
        List<Field> fieldList = new ArrayList<>();
        for (Class<?> clazz = tClass; clazz != Object.class && clazz != null; clazz = clazz.getSuperclass()) {
            Field[] fields = getField(clazz);
            if (CollectionUtil.isEmpty(fields)) {
                continue;
            }
            Collections.addAll(fieldList, fields);
        }
        return fieldList;
    }

    /**
     * java bean 转成map<fieldName,fieldValue>
     *
     * @param obj java bean with getter setter
     * @return
     */
    public static Map<String, Object> beanToMap(Object obj) {
        List<Field> fields = getAllField(obj.getClass());
        Map<String, Object> map = new HashMap<>((int) (fields.size() * 1.75) + 1);
        CollectionUtil.forEach(fields, x -> {
            try {
                map.put(x.getName(), invoke(obj, "get" + StringUtil.capitalize(x.getName())));
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        });
        return map;
    }

    public static <T> MethodAccess getMethodAccess(Class<T> clazz) {
        try {
            return CACHE_METHOD.getUnchecked(clazz);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public static Object invoke(Object obj, String methodName, Object... args) {
        MethodAccess methodAccess = getMethodAccess(obj.getClass());
        return methodAccess.invoke(obj, methodName, args);
    }

    public static Object invoke(Object obj, String methodName, Class[] paramTypes, Object... args) {
        MethodAccess methodAccess = getMethodAccess(obj.getClass());
        return methodAccess.invoke(obj, methodName, paramTypes, args);
    }

    public static <T, A extends Annotation> Field getField(Class<T> tClass, Class<A> annotationClass) {
        for (Class<?> clazz = tClass; clazz != Object.class && clazz != null; clazz = clazz.getSuperclass()) {
            Field[] fields = getField(clazz);
            if (fields != null && fields.length > 0) {
                for (Field f : fields) {
                    if (f.isAnnotationPresent(annotationClass)) {
                        return f;
                    }
                }
            }
        }
        return null;
    }

    public static <T, A extends Annotation> List<Field> getFields(Class<T> tClass, Class<A> annotationClass) {
        List<Field> fields = getAllField(tClass);
        return CollectionUtil.filter(fields, (Predicate<Field>) field -> field.isAnnotationPresent(annotationClass));
    }
}
