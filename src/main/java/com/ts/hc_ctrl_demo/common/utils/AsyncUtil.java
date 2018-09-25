package com.ts.hc_ctrl_demo.common.utils;

import com.ts.hc_ctrl_demo.common.entity.AsyncExcResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * 异步工具
 *
 * @author pant
 * @date 2018/3/30 14:39
 */
public class AsyncUtil {

    private static final Logger logger = LoggerFactory.getLogger(AsyncUtil.class);

    /**
     * 线程池参数意义:
     * 线程数，任务处理的最大线程数
     * 当超过消息缓冲队列后，允许的最大线程数
     * 线程空闲滞留时间
     * 时间为秒
     * 设置缓冲队列，及大小，可以设置无界，但是到达一定程度系统就奔溃了。
     * 当任务超过缓冲队列后做的处理：抛出异常
     */
    private static final ThreadPoolExecutor THREAD_POOL = getThreadPoolExecutor();

    /**
     * 提供统一的线程池执行器
     *
     * @return ThreadPoolExecutor
     * @author pant
     * @date 2018/5/7 16:18
     */
    private static ThreadPoolExecutor getThreadPoolExecutor() {
        /**
         * 线程池队列大小
         */
        int POOL_QUEUE_SIZE = 309600;
        return new ThreadPoolExecutor(12,
                24,
                5,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(POOL_QUEUE_SIZE),
                new ThreadPoolExecutor.AbortPolicy()
        );
    }

    private static final CompletionService<AsyncExcResult> COMPLETION_SERVICE = new ExecutorCompletionService<>(THREAD_POOL,
            new LinkedBlockingDeque<>(1000));

    /**
     * 获取线程池
     *
     * @return
     */
    private static ThreadManager getExecutorService() {
        ThreadManager threadManager = new ThreadManager();
        threadManager.setThreadPoolExecutor();
        threadManager.setCompletionService();
        return threadManager;
    }

    /**
     * 异步请求 runnable 无返回值
     *
     * @param runnable
     * @author pant
     * @date 2018/3/30 16:03
     */
    public static void runAsync(Runnable runnable) {
        getExecutorService().getThreadPoolExecutor().execute(runnable);
    }

    /**
     * 异步请求带返回值的callable
     *
     * @param callable
     * @return
     * @author pant
     * @date 2018/3/30 16:03
     */
    public static AsyncExcResult runAsyncCallable(Callable<AsyncExcResult> callable) {
        try {
            CompletionService<AsyncExcResult> completionService =  getExecutorService().getCompletionService();
            completionService.submit(callable);
            Future<AsyncExcResult> httpResultEntryFuture = completionService.take();
            return httpResultEntryFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Failed Execution error message: {}", e.getMessage());
            return AsyncExcResult.fail(e.getMessage());
        }
    }

    /**
     * 批量异步调用
     *
     * @param callables
     * @return
     * @author pant
     * @date 2018/3/30 16:02
     */
    public static List<AsyncExcResult> runAsyncCallable(List<Callable<AsyncExcResult>> callables) {
        return runAsyncCallable(callables, false);
    }

    /**
     * 批量异步调用 定制其中一个任务失败是否中断整个批量调用。
     *
     * @param callables
     * @param failedInterrupt
     * @return
     * @author pant
     * @date 2018/3/30 16:02
     */
    public static List<AsyncExcResult> runAsyncCallable(List<Callable<AsyncExcResult>> callables, boolean failedInterrupt) {
        CompletionService<AsyncExcResult> completionService = getExecutorService().getCompletionService();
        if (callables.isEmpty()) {
            return new ArrayList<>();
        }

        List<AsyncExcResult> resultList = new ArrayList<>(callables.size());

        try {

            for (Callable<AsyncExcResult> callable : callables) {
                completionService.submit(callable);
            }

            for (int i = 0; i < callables.size(); i++) {
                Future<AsyncExcResult> httpResultEntryFuture = completionService.take();
                AsyncExcResult asyncExcResult = httpResultEntryFuture.get();
                resultList.add(asyncExcResult);
                if (!asyncExcResult.isSuccess() && failedInterrupt) {
                    return resultList;
                }
            }

            return resultList;
        } catch (InterruptedException | ExecutionException e) {
            logger.error("batch execute failed", e);
            resultList.add(AsyncExcResult.fail(e.getMessage()));
            return resultList;
        }
    }

    private static class ThreadManager{
        private ThreadPoolExecutor threadPoolExecutor;
        private CompletionService<AsyncExcResult> completionService;

        private ThreadPoolExecutor getThreadPoolExecutor() {
            return threadPoolExecutor;
        }

        private void setThreadPoolExecutor() {
            this.threadPoolExecutor = AsyncUtil.THREAD_POOL;
        }

        private CompletionService<AsyncExcResult> getCompletionService() {
            return completionService;
        }

        private void setCompletionService() {
            this.completionService = AsyncUtil.COMPLETION_SERVICE;
        }
    }

}
