package com.xjbg.java.sdk.lock;

import java.util.concurrent.TimeUnit;

/**
 * 细粒度锁
 *
 * @author kesc
 * @since 2019/3/8
 */
public interface StripedLock {
    /**
     * 获取锁 一直阻塞
     *
     * @param key
     */
    void lock(String key);

    /**
     * 获取锁 超时返回false
     *
     * @param key
     * @param time
     * @param timeUnit
     * @return
     * @throws InterruptedException
     */
    boolean tryLock(String key, long time, TimeUnit timeUnit) throws InterruptedException;

    /**
     * 释放锁
     *
     * @param key
     */
    void unlock(String key);

    /**
     * 强制释放锁
     *
     * @param key
     */
    void forceUnlock(String key);

    /**
     * 当前线程是否持有锁
     *
     * @param key
     * @return
     */
    boolean isHeldByCurrentThread(String key);
}
