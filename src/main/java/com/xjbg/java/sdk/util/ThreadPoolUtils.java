package com.xjbg.java.sdk.util;

import java.util.concurrent.*;

/**
 * @author: kesc
 * @time: 2017-12-19 20:05
 */
public class ThreadPoolUtils {
    private static final ThreadPoolExecutor POOL = create();
    private static final ScheduledThreadPoolExecutor SCHEDULED_POOL = createScheduledPool(Runtime.getRuntime().availableProcessors() * 2);

    static {
        POOL.allowCoreThreadTimeOut(true);
    }

    public static ThreadPoolExecutor create() {
        return create(Runtime.getRuntime().availableProcessors() * 2);
    }

    public static ThreadPoolExecutor create(int coreSize) {
        return create(coreSize, coreSize * 2);
    }

    public static ThreadPoolExecutor create(int coreSize, int maxSize) {
        return create(coreSize, maxSize, 1000);
    }

    public static ThreadPoolExecutor create(int coreSize, int maxSize, int queueSize) {
        return create(coreSize, maxSize, queueSize, Executors.defaultThreadFactory(), new ThreadPoolExecutor.CallerRunsPolicy());
    }

    public static ThreadPoolExecutor create(int coreSize, int maxSize,
                                            int queueSize,
                                            RejectedExecutionHandler rejectedExecutionHandler) {
        return create(coreSize, maxSize, queueSize, Executors.defaultThreadFactory(), rejectedExecutionHandler);
    }

    public static ThreadPoolExecutor create(int coreSize, int maxSize,
                                            int queueSize,
                                            ThreadFactory threadFactory,
                                            RejectedExecutionHandler rejectedExecutionHandler) {
        return create(coreSize, maxSize, queueSize, 300L, TimeUnit.SECONDS, threadFactory, rejectedExecutionHandler);
    }

    public static ThreadPoolExecutor create(int coreSize, int maxSize,
                                            int queueSize,
                                            long keepAliveTime, TimeUnit keepAliveTimeUnit,
                                            ThreadFactory threadFactory,
                                            RejectedExecutionHandler rejectedExecutionHandler) {
        return new ThreadPoolExecutor(coreSize,
                maxSize,
                keepAliveTime, keepAliveTimeUnit,
                new LinkedBlockingQueue<>(queueSize),
                threadFactory,
                rejectedExecutionHandler);
    }

    public static ScheduledThreadPoolExecutor createScheduledPool(int size) {
        return createScheduledPool(size, new ThreadPoolExecutor.CallerRunsPolicy());
    }

    public static ScheduledThreadPoolExecutor createScheduledPool(int size, RejectedExecutionHandler rejectedExecutionHandler) {
        return createScheduledPool(size, Executors.defaultThreadFactory(), rejectedExecutionHandler);
    }

    public static ScheduledThreadPoolExecutor createScheduledPool(int size,
                                                                  ThreadFactory threadFactory,
                                                                  RejectedExecutionHandler rejectedExecutionHandler) {
        return new ScheduledThreadPoolExecutor(size, threadFactory, rejectedExecutionHandler);
    }

    public static void execute(Runnable runnable) {
        POOL.execute(runnable);
    }

    public static Future<?> submit(Runnable runnable) {
        return POOL.submit(runnable);
    }

    public static <V> Future<V> submit(Callable<V> callable) {
        return POOL.submit(callable);
    }

    public static Future<?> submitSchedule(Runnable runnable) {
        return SCHEDULED_POOL.submit(runnable);
    }

    public static <V> Future<V> submitSchedule(Callable<V> callable) {
        return SCHEDULED_POOL.submit(callable);
    }

    public static ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
        return SCHEDULED_POOL.schedule(command, delay, unit);
    }

    public static ScheduledFuture<?> scheduleAtFixedRate(Runnable command,
                                                         long initialDelay,
                                                         long period,
                                                         TimeUnit unit) {
        return SCHEDULED_POOL.scheduleAtFixedRate(command, initialDelay, period, unit);
    }

    public static ScheduledFuture<?> scheduleWithFixedDelay(Runnable command,
                                                            long initialDelay,
                                                            long delay,
                                                            TimeUnit unit) {
        return SCHEDULED_POOL.scheduleWithFixedDelay(command, initialDelay, delay, unit);
    }
}
