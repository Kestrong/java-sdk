package com.xjbg.java.sdk.util;

import java.util.concurrent.*;

/**
 * @author: kesc
 * @time: 2017-12-19 20:05
 */
public class ThreadPoolUtils {
    private static final ThreadPoolExecutor POOL = create(5);
    private static final ScheduledThreadPoolExecutor SCHEDULED_POOL = createScheduledPool(5);

    /**
     * @param size
     * @return
     */
    public static ThreadPoolExecutor create(int size) {
        return new ThreadPoolExecutor(Math.min(size, Runtime.getRuntime().availableProcessors()),
                Math.max(size * 2, Runtime.getRuntime().availableProcessors()),
                120L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(1000),
                new ThreadPoolExecutor.CallerRunsPolicy());
    }

    /**
     * @param size
     * @return
     */
    public static ThreadPoolExecutor create(int size, int queueSize) {
        return new ThreadPoolExecutor(Math.min(size, Runtime.getRuntime().availableProcessors()),
                Math.max(size * 2, Runtime.getRuntime().availableProcessors()),
                120L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(queueSize),
                new ThreadPoolExecutor.CallerRunsPolicy());
    }

    public static ScheduledThreadPoolExecutor createScheduledPool(int size) {
        return new ScheduledThreadPoolExecutor(size, new ThreadPoolExecutor.CallerRunsPolicy());
    }

    public static void execute(Runnable runnable) {
        POOL.execute(runnable);
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
