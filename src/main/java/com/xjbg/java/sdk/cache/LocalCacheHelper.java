package com.xjbg.java.sdk.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.SneakyThrows;
import org.apache.commons.collections4.map.LRUMap;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * memory cache using guava cache, be careful of modifying cache items if necessary
 *
 * @author kesc
 * @date 2021-06-22 15:28
 */
@SuppressWarnings(value = "unused")
public class LocalCacheHelper {
    private static final Map<String, Cache<String, Object>> CACHE_MAP = new LRUMap<>();
    public static final String DEFAULT_CACHE_TYPE = "defaultCacheType";
    private static final byte[] LOCK = new byte[0];
    private static final Integer DEFAULT_MAX_SIZE = Integer.valueOf(System.getProperty("local.cache.size", "1000"));
    private static final Integer DEFAULT_DURATION = Integer.valueOf(System.getProperty("local.cache.duration", "30"));
    private static final TimeUnit DEFAULT_TIME_UNIT = TimeUnit.MINUTES;

    static {
        getCache(DEFAULT_CACHE_TYPE);
    }

    private static boolean cacheEnable() {
        return Boolean.parseBoolean(System.getProperty("local.cache.enable", "true"));
    }

    public static <K, V> LoadingCache<K, V> buildCache(Integer maxSize, Integer duration, TimeUnit timeUnit,
                                                       Function<K, V> function) {
        if (!cacheEnable()) {
            maxSize = 0;
            duration = 0;
        }
        return CacheBuilder.newBuilder().maximumSize(maxSize).expireAfterWrite(duration, timeUnit)
                .build(new CacheLoader<K, V>() {
                    @Override
                    public V load(@Nonnull K key) {
                        return function.apply(key);
                    }
                });
    }

    public static <K, V> LoadingCache<K, V> buildCache(Function<K, V> function) {
        return buildCache(DEFAULT_MAX_SIZE, DEFAULT_DURATION, DEFAULT_TIME_UNIT, function);
    }

    public static <K, V> Cache<K, V> buildCache(Integer maxSize, Integer duration, TimeUnit timeUnit) {
        if (!cacheEnable()) {
            maxSize = 0;
            duration = 0;
        }
        return CacheBuilder.newBuilder().maximumSize(maxSize).expireAfterWrite(duration, timeUnit).build();
    }

    public static <K, V> Cache<K, V> buildCache() {
        return buildCache(DEFAULT_MAX_SIZE, DEFAULT_DURATION, DEFAULT_TIME_UNIT);
    }

    private static Cache<String, Object> getCache(String cacheType) {
        if (CACHE_MAP.containsKey(cacheType)) {
            return CACHE_MAP.get(cacheType);
        }
        synchronized (LOCK) {
            if (CACHE_MAP.containsKey(cacheType)) {
                return CACHE_MAP.get(cacheType);
            }
            return CACHE_MAP.compute(cacheType, (s, k) -> buildCache());
        }
    }

    @SneakyThrows
    @SuppressWarnings(value = "unchecked")
    public static <V> V get(String cacheType, String key, Callable<V> loader) {
        if (!cacheEnable()) {
            if (loader == null) {
                return null;
            }
            return loader.call();
        }
        Cache<String, Object> cache = getCache(cacheType);
        try {
            if (loader == null) {
                return (V) cache.getIfPresent(key);
            }
            return (V) cache.get(key, loader);
        } catch (Exception e) {
            return null;
        }
    }

    public static <V> V get(String key, Callable<V> loader) {
        return get(DEFAULT_CACHE_TYPE, key, loader);
    }

    public static <V> V get(String cacheType, String key) {
        return get(cacheType, key, null);
    }

    public static <V> V get(String key) {
        return get(DEFAULT_CACHE_TYPE, key);
    }

    public static <V> Map<String, V> get(String cacheType, List<String> keys, Callable<V> loader) {
        Map<String, V> result = new HashMap<>(keys.size());
        for (String key : keys) {
            V o = get(cacheType, key, loader);
            result.put(key, o);
        }
        return result;
    }

    public static <V> Map<String, V> get(List<String> keys, Callable<V> loader) {
        return get(DEFAULT_CACHE_TYPE, keys, loader);
    }

    public static <V> Map<String, V> get(String cacheType, List<String> keys) {
        return get(cacheType, keys, null);
    }

    public static <V> Map<String, V> get(List<String> keys) {
        return get(DEFAULT_CACHE_TYPE, keys);
    }

    public static void set(String cacheType, String key, Object value) {
        getCache(cacheType).put(key, value);
    }

    public static void set(String key, Object value) {
        set(DEFAULT_CACHE_TYPE, key, value);
    }

    public static void set(String cacheType, Map<String, Object> items) {
        getCache(cacheType).putAll(items);
    }

    public static void set(Map<String, Object> items) {
        set(DEFAULT_CACHE_TYPE, items);
    }

    public static void remove(String cacheType, String key) {
        Cache<String, Object> loadingCache = CACHE_MAP.get(cacheType);
        if (loadingCache != null) {
            loadingCache.invalidate(key);
        }
    }

    public static void remove(String key) {
        remove(DEFAULT_CACHE_TYPE, key);
    }

    public static void removeType(String cacheType) {
        Cache<String, Object> loadingCache = CACHE_MAP.get(cacheType);
        if (loadingCache != null) {
            loadingCache.invalidateAll();
        }
    }

    public static void removeType() {
        removeType(DEFAULT_CACHE_TYPE);
    }

    public static void clear() {
        for (Map.Entry<String, Cache<String, Object>> entry : CACHE_MAP.entrySet()) {
            entry.getValue().invalidateAll();
        }
    }
}
