package com.micropower.basic.util;

import java.util.concurrent.*;

/**
 * @Date: 2021/1/18 10:31
 * @Description: TODO → 线程工具类
 * @Author:Kohaku_川
 **/
public class ThreadPoolUtil {

    private static ThreadPoolExecutor threadPool;

    /**
     * 无返回值直接执行
     *
     * @param runnable
     */
    public static void execute(Runnable runnable) {
        getThreadPool().execute(runnable);
    }

    /**
     * 返回值直接执行
     *
     * @param callable
     */
    public static <T> Future<T> submit(Callable<T> callable) {
        return getThreadPool().submit(callable);
    }


    /**
     * dcs获取线程池
     *
     * @return 线程池对象
     */
    private static ThreadPoolExecutor getThreadPool() {
        if (threadPool != null) {
            return threadPool;
        } else {
            synchronized (ThreadPoolUtil.class) {
                if (threadPool == null) {
                    threadPool = new ThreadPoolExecutor(100, 300, 60, TimeUnit.SECONDS,
                            new LinkedBlockingQueue<>(32), new ThreadPoolExecutor.CallerRunsPolicy());
                }
                return threadPool;
            }
        }
    }
}
