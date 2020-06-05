package com.xjbg.java.sdk.util;

import java.util.concurrent.*;

/**
 * @author: kesc
 * @time: 2017-12-19 20:05
 */
public class ThreadPoolUtils {
    private static final ThreadPoolExecutor POOL = create(8);
    private static final ScheduledThreadPoolExecutor SCHEDULED_POOL = createScheduledPool(8);

    public static ThreadPoolExecutor create(int size) {
        return create(size, 1000);
    }

    public static ThreadPoolExecutor create(int size, int queueSize) {
        return create(size, queueSize, Executors.defaultThreadFactory(), new ThreadPoolExecutor.CallerRunsPolicy());
    }

    public static ThreadPoolExecutor create(int size,
                                            int queueSize,
                                            RejectedExecutionHandler rejectedExecutionHandler) {
        return create(size, queueSize, Executors.defaultThreadFactory(), rejectedExecutionHandler);
    }

    public static ThreadPoolExecutor create(int size,
                                            int queueSize,
                                            ThreadFactory threadFactory,
                                            RejectedExecutionHandler rejectedExecutionHandler) {
        return new ThreadPoolExecutor(Math.min(size, Runtime.getRuntime().availableProcessors()),
                Math.max(size * 2, Runtime.getRuntime().availableProcessors()),
                120L, TimeUnit.SECONDS,
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

    public static Future<?> submitSchedule(Runnable runnable) {
        return SCHEDULED_POOL.submit(runnable);
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
