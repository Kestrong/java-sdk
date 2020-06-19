package com.xjbg.java.sdk.lock.impl;

import com.xjbg.java.sdk.lock.StripedLock;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 细粒度的可重入锁 内存锁 不可用于分布式集群环境
 *
 * @author kesc
 * @date 2020-06-18 15:59
 */
@Slf4j
public class DefaultReentrantStripedLock implements StripedLock {
    private static final byte[] LOCK = new byte[0];
    private final Map<String, ReentrantLock> lockMap = new HashMap<>();

    @Override
    public void lock(String key) {
        if (!lockMap.containsKey(key)) {
            synchronized (LOCK) {
                if (!lockMap.containsKey(key)) {
                    lockMap.put(key, new ReentrantLock());
                }
            }
        }
        lockMap.get(key).lock();
        log.debug("get lock by key:{}", key);
    }

    @Override
    public boolean tryLock(String key, long timeout, TimeUnit timeUnit) throws InterruptedException {
        if (!lockMap.containsKey(key)) {
            synchronized (LOCK) {
                if (!lockMap.containsKey(key)) {
                    lockMap.put(key, new ReentrantLock());
                }
            }
        }
        boolean tryLock = lockMap.get(key).tryLock(timeout, timeUnit);
        log.debug("try lock by key:{},timeout:{},timeUnit:{},success:{} ", key, timeout, timeUnit.toString(),
                tryLock);
        return tryLock;
    }

    @Override
    public void unlock(String key) {
        if (isHeldByCurrentThread(key)) {
            lockMap.get(key).unlock();
            log.debug("unlock by key:{}", key);
        }
    }

    @Override
    public void forceUnlock(String key) {
        ReentrantLock lock = lockMap.get(key);
        if (lock == null) {
            return;
        }
        lock.unlock();
        log.debug("force unlock by key:{}", key);
    }

    @Override
    public boolean isHeldByCurrentThread(String key) {
        ReentrantLock lock = lockMap.get(key);
        boolean flag = lock != null && lock.isHeldByCurrentThread();
        log.debug("is held by current thread,flag:{}, key:{}", flag, key);
        return flag;
    }
}
