package com.xjbg.java.sdk.lock.impl;

import com.xjbg.java.sdk.lock.DistributedLock;
import com.xjbg.java.sdk.util.JsonUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.DefaultStringRedisConnection;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StopWatch;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author kesc
 * @since 2019/3/8
 */
@Slf4j
@Getter
@Setter
public final class DefaultDistributedLock implements DistributedLock {
    private static final long LONG_ZERO = 0L;
    private static final String PREFIX = "lock:";
    private static final long DEFAULT_SLEEP_TIME = 10L;
    private int maxLeaseTime = 60000;
    private StringRedisTemplate stringRedisTemplate;
    private static ThreadLocal<UUID> threadUuid = new ThreadLocal<>();

    @Override
    public void lock(String key) {
        boolean isLocked = this.lockInner(key, this.maxLeaseTime, 0L);
        if (!isLocked) {
            throw new RuntimeException("error occur when lock");
        }
    }

    @Override
    public boolean tryLock(String key, long time, TimeUnit unit) {
        long waitTime = unit.toMillis(time);
        return this.lockInner(key, this.maxLeaseTime, waitTime);
    }

    @Override
    public void unlock(String key) {
        if (this.isHeldByCurrentThread(key)) {
            this.unlockInner(key);
            threadUuid.remove();
        }

    }

    @Override
    public void forceUnlock(String key) {
        this.unlockInner(key);
        log.debug("force unlocked, key={}", key);
    }

    @Override
    public boolean isHeldByCurrentThread(String key) {
        DefaultDistributedLock.LockValue lockValue = this.getCurrentLock(key);
        return lockValue != null && Objects.equals(threadUuid.get(), lockValue.getId());
    }

    private DefaultDistributedLock.LockValue getCurrentLock(String key) {
        String lockKey = this.getKey(key);
        String value = this.stringRedisTemplate.opsForValue().get(lockKey);
        return value == null ? null : JsonUtil.toObject(value, DefaultDistributedLock.LockValue.class);
    }

    private String getKey(String key) {
        return "lock:" + key;
    }

    private boolean lockInner(String key, int leaseTime, long timeout) {
        String lockKey = this.getKey(key);
        try {
            boolean isTry = timeout > 0L;
            String value = JsonUtil.toJsonString(new DefaultDistributedLock.LockValue());
            log.debug("begin try lock, isTry={}, timeout={}, key={}", isTry, timeout, lockKey);
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            stopWatch.stop();
            while (!isTry || stopWatch.getTotalTimeMillis() <= timeout) {
                stopWatch.start();
                Boolean success = setIfAbsent(this.stringRedisTemplate, lockKey, value, (long) leaseTime, TimeUnit.MILLISECONDS);
                if (success) {
                    log.debug("locked, key= {}, value={}", lockKey, value);
                    return true;
                }
                stopWatch.stop();
                Thread.sleep(10L);
            }
            log.info("fail to lock cause by timeout, key={}", lockKey);
            return false;
        } catch (Exception e) {
            log.error("error occur when lock, key=" + lockKey, e);
            return false;
        }
    }

    private void unlockInner(String key) {
        String lockKey = this.getKey(key);
        this.stringRedisTemplate.delete(lockKey);
    }

    private Boolean setIfAbsent(StringRedisTemplate stringRedisTemplate, String key, String value, long timeout, TimeUnit unit) {
        return stringRedisTemplate.execute((RedisCallback<Boolean>) connection -> {
            StringRedisConnection stringRedisConnection = new DefaultStringRedisConnection(connection);
            Object executeResult = stringRedisConnection.execute("set", key, value, "px", String.valueOf(unit.toMillis(timeout)), "NX");
            return executeResult != null;
        });
    }

    static class LockValue {
        private UUID id = UUID.randomUUID();
        private Long threadId;

        public LockValue() {
            DefaultDistributedLock.threadUuid.set(this.id);
            this.threadId = Thread.currentThread().getId();
        }

        public UUID getId() {
            return this.id;
        }

        public void setId(UUID id) {
            this.id = id;
            DefaultDistributedLock.threadUuid.set(this.id);
        }

        public Long getThreadId() {
            return this.threadId;
        }

        public void setThreadId(Long threadId) {
            this.threadId = threadId;
        }
    }
}
