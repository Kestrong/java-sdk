package com.xjbg.java.sdk.cache;


import com.google.common.collect.Sets;
import com.xjbg.java.sdk.util.CollectionUtil;
import com.xjbg.java.sdk.util.JsonUtil;
import com.xjbg.java.sdk.util.StringUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.Assert;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author kesc
 * @since 2019/3/1
 */
@Getter
@Setter
@Slf4j
public class CacheTemplate {
    private String version = "V1";
    private StringRedisTemplate stringRedisTemplate;
    private StringRedisSerializer redisSerializer;


    private void assertNotNull(Object target) {
        Assert.notNull(target, "cache key/value was null");
    }

    private void assertNotEmpty(Collection<String> targets) {
        Assert.notEmpty(targets, "cache keys/values was empty");
    }

    public String key(String key) {
        return key + ":" + version;
    }

    public Collection<String> keys(Collection<String> keys) {
        Collection<String> collection = new ArrayList<>(keys.size());
        CollectionUtil.forEach(keys, x -> collection.add(this.key(x)));
        return collection;
    }

    public Boolean exist(String key) {
        assertNotNull(key);
        return this.stringRedisTemplate.hasKey(this.key(key));
    }

    public void remove(String key) {
        assertNotNull(key);
        this.stringRedisTemplate.delete(this.key(key));
    }

    public void remove(Collection<String> keys) {
        assertNotEmpty(keys);
        this.stringRedisTemplate.delete(this.keys(keys));
    }

    public DataType type(String key) {
        assertNotNull(key);
        return this.stringRedisTemplate.type(this.key(key));
    }

    public Long getExpire(String key, TimeUnit unit) {
        assertNotNull(key);
        return this.stringRedisTemplate.getExpire(this.key(key), unit);
    }

    public Boolean expire(String key, Long timeout, TimeUnit unit) {
        assertNotNull(key);
        return this.stringRedisTemplate.expire(this.key(key), timeout, unit);
    }

    public Boolean expire(Collection<String> keys, Long timeout, TimeUnit unit) {
        Collection<String> keyWithVersions = this.keys(keys);
        long rawTimeout = TimeoutUtils.toMillis(timeout, unit);
        return this.stringRedisTemplate.execute((RedisCallback<Boolean>) connection -> {
            for (String key : keyWithVersions) {
                byte[] rawKey = redisSerializer.serialize(key);
                try {
                    connection.pExpire(rawKey, rawTimeout);
                } catch (Exception var11) {
                    connection.expire(rawKey, TimeoutUtils.toSeconds(timeout, unit));
                }
            }
            return true;
        });
    }

    public void expireAt(String key, Date expireDate) {
        assertNotNull(key);
        this.stringRedisTemplate.expireAt(this.key(key), expireDate);
    }

    public String valueGet(String key) {
        assertNotNull(key);
        return this.stringRedisTemplate.opsForValue().get(this.key(key));
    }

    public <T> T valueGet(String key, Class<T> valueClass) {
        String valueJson = valueGet(key);
        if (StringUtil.isEmpty(valueJson)) {
            return null;
        }
        return JsonUtil.toObject(valueJson, valueClass);
    }

    public <T> List<T> valueGetList(String key, Class<T> valueClass) {
        assertNotNull(key);
        String valueJson = this.stringRedisTemplate.opsForValue().get(this.key(key));
        return JsonUtil.toList(valueJson, valueClass);
    }

    public List<String> valueMultiGet(Collection<String> keys) {
        assertNotEmpty(keys);
        return this.stringRedisTemplate.opsForValue().multiGet(this.keys(keys));
    }

    public <T> List<T> valueMultiGet(Collection<String> keys, Class<T> valueClass) {
        assertNotEmpty(keys);
        List<String> valueJsonList = this.stringRedisTemplate.opsForValue().multiGet(this.keys(keys));
        if (CollectionUtil.isEmpty(valueJsonList)) {
            return Collections.emptyList();
        }
        List<T> values = new ArrayList<>(keys.size());
        for (String value : valueJsonList) {
            values.add(JsonUtil.toObject(value, valueClass));
        }
        return values;
    }

    public <T> void valueSet(String key, T value) {
        assertNotNull(key);
        assertNotNull(value);
        this.stringRedisTemplate.opsForValue().set(this.key(key), JsonUtil.toJsonString(value));
    }

    public <T> void valueSet(String key, T value, Long timeout, TimeUnit timeUnit) {
        assertNotNull(key);
        assertNotNull(value);
        assertNotNull(timeout);
        assertNotNull(timeUnit);
        this.stringRedisTemplate.opsForValue().set(this.key(key), JsonUtil.toJsonString(value), timeout, timeUnit);
    }

    public <T> void valueMultiSet(Map<String, T> entries) {
        Map<String, String> map = this.genStringMap(entries);
        this.stringRedisTemplate.opsForValue().multiSet(map);
    }

    public <T> void valueMultiSet(Map<String, T> entries, Long timeout, TimeUnit timeUnit) {
        Map<String, String> map = this.genStringMap(entries);
        this.stringRedisTemplate.opsForValue().multiSet(map);
        this.expire(map.keySet(), timeout, timeUnit);
    }

    public <T> Boolean valueSetIfAbsent(String key, T value) {
        assertNotNull(key);
        assertNotNull(value);
        return this.stringRedisTemplate.opsForValue().setIfAbsent(this.key(key), JsonUtil.toJsonString(value));
    }

    public <T> Boolean valueSetIfAbsent(String key, T value, Long timeout, TimeUnit unit) {
        assertNotNull(key);
        assertNotNull(value);
        String keyWithVersion = this.key(key);
        String valueJson = JsonUtil.toJsonString(value);
        long rawTimeout = TimeoutUtils.toMillis(timeout, unit);
        return this.stringRedisTemplate.execute((RedisCallback<Boolean>) connection -> {
            byte[] rawKey = redisSerializer.serialize(keyWithVersion);
            byte[] rawValue = redisSerializer.serialize(valueJson);
            Boolean success = connection.setNX(rawKey, rawValue);
            if (success) {
                connection.pExpire(rawKey, rawTimeout);
            }
            return success;
        });
    }

    public Long valueIncrement(String key) {
        assertNotNull(key);
        return this.stringRedisTemplate.opsForValue().increment(this.key(key), 1L);
    }

    public Long valueIncrement(String key, Long increment) {
        assertNotNull(key);
        return this.stringRedisTemplate.opsForValue().increment(this.key(key), increment);
    }

    public String hashGet(String key, String hashKey) {
        assertNotNull(key);
        assertNotNull(hashKey);
        return (String) this.stringRedisTemplate.opsForHash().get(this.key(key), hashKey);
    }

    public <T> T hashGet(String key, String hashKey, Class<T> hashValueClass) {
        String hashValueJson = this.hashGet(key, hashKey);
        return StringUtil.isNotBlank(hashValueJson) ? JsonUtil.toObject(hashValueJson, hashValueClass) : null;
    }

    public <T> List<T> hashGetList(String key, String hashKey, Class<T> valueClass) {
        assertNotNull(key);
        assertNotNull(hashKey);
        String hashValueJson = (String) this.stringRedisTemplate.opsForHash().get(this.key(key), hashKey);
        return StringUtil.isNotBlank(hashValueJson) ? JsonUtil.toList(hashValueJson, valueClass) : Collections.emptyList();
    }

    public <T> List<T> hashMultiGet(String key, Collection<String> hashKeys, Class<T> hashValueClass) {
        assertNotNull(key);
        assertNotEmpty(hashKeys);
        BoundHashOperations<String, String, String> boundHashOperations = this.stringRedisTemplate.boundHashOps(this.key(key));
        List<String> hashValueJsonList = boundHashOperations.multiGet(hashKeys);
        if (CollectionUtil.isEmpty(hashValueJsonList)) {
            return Collections.emptyList();
        }
        List<T> values = new ArrayList<>(hashKeys.size());
        for (String value : hashValueJsonList) {
            values.add(JsonUtil.toObject(value, hashValueClass));
        }
        return values;
    }

    public <T> List<List<T>> hashMultiGetList(String key, Collection<String> hashKeys, Class<T> hashValueClass) {
        assertNotNull(key);
        assertNotEmpty(hashKeys);
        BoundHashOperations<String, String, String> boundHashOperations = this.stringRedisTemplate.boundHashOps(this.key(key));
        List<String> hashValueJsonList = boundHashOperations.multiGet(hashKeys);
        if (CollectionUtil.isEmpty(hashValueJsonList)) {
            return Collections.emptyList();
        }
        List<List<T>> values = new ArrayList<>(hashKeys.size());
        for (String value : hashValueJsonList) {
            values.add(JsonUtil.toList(value, hashValueClass));
        }
        return values;
    }

    public Map<String, String> hashGetAll(String key) {
        assertNotNull(key);
        BoundHashOperations<String, String, String> boundHashOperations = this.stringRedisTemplate.boundHashOps(this.key(key));
        return boundHashOperations.entries();
    }

    public <T> Map<String, T> hashGetAll(String key, Class<T> hashValueClass) {
        assertNotNull(key);
        BoundHashOperations<String, String, String> boundHashOperations = this.stringRedisTemplate.boundHashOps(this.key(key));
        Map<String, String> entries = boundHashOperations.entries();
        if (CollectionUtil.isEmpty(entries)) {
            return Collections.emptyMap();
        }
        Map<String, T> map = new HashMap<>(entries.size());
        for (Map.Entry<String, String> entry : entries.entrySet()) {
            map.put(entry.getKey(), JsonUtil.toObject(entry.getValue(), hashValueClass));
        }
        return map;
    }

    public <T> void hashPut(String key, String hashKey, T hashValue) {
        assertNotNull(key);
        assertNotNull(hashKey);
        assertNotNull(hashValue);
        this.stringRedisTemplate.opsForHash().put(this.key(key), hashKey, JsonUtil.toJsonString(hashValue));
    }

    public <T> void hashMultiPut(String key, Map<String, T> entries) {
        assertNotNull(key);
        if (CollectionUtil.isEmpty(entries)) {
            return;
        }
        Map<String, String> map = this.genStringMap(entries);
        this.stringRedisTemplate.opsForHash().putAll(this.key(key), map);
    }

    public <T> Boolean hashPutIfAbsent(String key, String hashKey, T hashValue) {
        assertNotNull(key);
        assertNotNull(hashKey);
        assertNotNull(hashValue);
        return this.stringRedisTemplate.opsForHash().putIfAbsent(this.key(key), hashKey, JsonUtil.toJsonString(hashValue));
    }

    public Boolean hashExists(String key, String hashKey) {
        assertNotNull(key);
        assertNotNull(hashKey);
        return this.stringRedisTemplate.opsForHash().hasKey(this.key(key), hashKey);
    }

    public void hashRemove(String key, String... hashKeys) {
        assertNotNull(key);
        assertNotNull(hashKeys);
        this.stringRedisTemplate.opsForHash().delete(this.key(key), hashKeys);
    }

    public Long hashSize(String key) {
        assertNotNull(key);
        return this.stringRedisTemplate.opsForHash().size(this.key(key));
    }

    public Set<String> hashKeys(String key) {
        assertNotNull(key);
        Set<Object> hashKeys = this.stringRedisTemplate.opsForHash().keys(this.key(key));
        if (CollectionUtil.isEmpty(hashKeys)) {
            return Collections.emptySet();
        }
        Set<String> hashKeySet = Sets.newHashSet();
        for (Object hashKey : hashKeys) {
            hashKeySet.add(String.valueOf(hashKey));
        }
        return hashKeySet;
    }

    public Long hashIncrement(String key, String hashKey) {
        assertNotNull(key);
        assertNotNull(hashKey);
        return this.stringRedisTemplate.opsForHash().increment(this.key(key), hashKey, 1L);
    }

    public Long hashIncrement(String key, String hashKey, Long increment) {
        return this.stringRedisTemplate.opsForHash().increment(this.key(key), hashKey, increment.longValue());
    }

    public void zSetAdd(String key, String value, double score) {
        assertNotNull(key);
        assertNotNull(value);
        assertNotNull(score);
        this.stringRedisTemplate.opsForZSet().add(this.key(key), value, score);
    }

    public void zSetRemove(String key, String... value) {
        assertNotNull(key);
        this.stringRedisTemplate.opsForZSet().remove(this.key(key), value);
    }

    public void zSetRemoveRangeByRank(String key, long start, long end) {
        assertNotNull(key);
        this.stringRedisTemplate.opsForZSet().removeRange(this.key(key), start, end);
    }

    public Set<String> zSetReverseRange(String key, long start, long end) {
        assertNotNull(key);
        return this.stringRedisTemplate.opsForZSet().reverseRange(this.key(key), start, end);
    }

    public Set<String> zSetReverseRangeAll(String key) {
        assertNotNull(key);
        return this.stringRedisTemplate.opsForZSet().reverseRange(this.key(key), 0L, -1L);
    }

    public Integer zSetReverseRank(String key, String value) {
        assertNotNull(key);
        assertNotNull(value);
        return this.stringRedisTemplate.opsForZSet().reverseRank(this.key(key), value).intValue();
    }

    public Long zSetSize(String key) {
        assertNotNull(key);
        return this.stringRedisTemplate.opsForZSet().size(this.key(key));
    }

    public Long currentTimeMillis() {
        return this.stringRedisTemplate.execute((RedisCallback<Long>) connection -> {
            List<byte[]> resultList = (List) connection.execute("TIME");
            if (CollectionUtil.isNotEmpty(resultList) && resultList.size() == 2) {
                Long currentUnixTime = Long.parseLong(new String(resultList.get(0)));
                Long costMicrosecond = Long.parseLong(new String(resultList.get(1)));
                return (currentUnixTime + costMicrosecond / 1000000L) * 1000L;
            }
            return System.currentTimeMillis();

        });
    }

    private <T> Map<String, String> genStringMap(Map<String, T> entries) {
        Map<String, String> map = new HashMap<>(entries.size());
        for (Map.Entry<String, T> entry : entries.entrySet()) {
            map.put(entry.getKey(), JsonUtil.toJsonString(entry.getValue()));
        }
        return map;
    }

    public Set<String> members(String key) {
        return getStringRedisTemplate().boundSetOps(key).members();
    }

    public <V> Set<V> members(String key, Class<V> vClass) {
        Set<String> members = members(key);
        if (CollectionUtil.isEmpty(members)) {
            return Collections.emptySet();
        }
        Set<V> result = new HashSet<>(members.size());
        for (String s : members) {
            result.add(JsonUtil.toObject(s, vClass));
        }
        return result;
    }

    public boolean isMember(String key, String member) {
        return getStringRedisTemplate().boundSetOps(key).isMember(member);
    }

    public <V> void memberAdd(String key, List<V> members) {
        if (CollectionUtil.isEmpty(members)) {
            return;
        }
        int size = members.size();
        String[] vs = new String[size];
        for (int i = 0; i < size; i++) {
            V v = members.get(i);
            if (v instanceof String) {
                vs[i] = (String) v;
            } else {
                vs[i] = JsonUtil.toJsonString(v);
            }
        }
        getStringRedisTemplate().boundSetOps(key).add(vs);
    }

    public <V> V memberPop(String key, Class<V> vClass) {
        String pop = getStringRedisTemplate().boundSetOps(key).pop();
        if (StringUtil.isBlank(pop)) {
            return null;
        }
        return JsonUtil.toObject(pop, vClass);
    }

    public <V> List<V> randomMember(String key, Integer num, Class<V> vClass) {
        Set<String> randomMembers = getStringRedisTemplate().boundSetOps(key).distinctRandomMembers(num);
        if (CollectionUtil.isEmpty(randomMembers)) {
            return Collections.emptyList();
        }
        List<V> result = new ArrayList<>(randomMembers.size());
        for (String s : randomMembers) {
            result.add(JsonUtil.toObject(s, vClass));
        }
        return result;
    }

    public <V> void removeMember(String key, List<V> members) {
        if (CollectionUtil.isEmpty(members)) {
            return;
        }
        int size = members.size();
        Object[] vs = new String[size];
        for (int i = 0; i < size; i++) {
            V v = members.get(i);
            if (v instanceof String) {
                vs[i] = v;
            } else {
                vs[i] = JsonUtil.toJsonString(v);
            }
        }
        getStringRedisTemplate().boundSetOps(key).remove(vs);
    }

    public <V> List<V> randomAndRemoveMembers(String key, Integer num, Class<V> vClass) {
        BoundSetOperations<String, String> ops = getStringRedisTemplate().boundSetOps(key);
        Set<String> randomMembers = ops.distinctRandomMembers(num);
        if (CollectionUtil.isEmpty(randomMembers)) {
            return Collections.emptyList();
        }
        List<V> result = new ArrayList<>(randomMembers.size());
        for (String s : randomMembers) {
            result.add(JsonUtil.toObject(s, vClass));
        }
        ops.remove(randomMembers.toArray());
        return result;
    }

    public Long memberSize(String key) {
        return getStringRedisTemplate().boundSetOps(key).size();
    }

}
