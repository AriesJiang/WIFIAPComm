package com.niqiu.net.tcp;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

//	ThreadPoolExecutor executor = new ThreadPoolExecutor(2, 5, 200, TimeUnit.MILLISECONDS,
//            new LinkedBlockingQueue<Runnable>() );

public class NetTCPThreadPoolExecutor implements Executor {

    private static final int CORE_POOL_SIZE = 5;
    private static final int MAXIMUM_POOL_SIZE = 256;
    private static final int KEEP_ALIVE = 1;

    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "PriorityExecutor #"
                    + mCount.getAndIncrement());
        }
    };

    private final ThreadPoolExecutor mThreadPoolExecutor;
    private static NetTCPThreadPoolExecutor mExecutor;

    public static NetTCPThreadPoolExecutor getInstance() {
        if (mExecutor == null) {
            return new NetTCPThreadPoolExecutor(CORE_POOL_SIZE);
        }
        return mExecutor;
    }

    public NetTCPThreadPoolExecutor getInstance(int poolSize) {
        if (mExecutor == null) {
            return new NetTCPThreadPoolExecutor(poolSize);
        }
        return mExecutor;
    }

    private NetTCPThreadPoolExecutor() {
        this(CORE_POOL_SIZE);
    }

    private NetTCPThreadPoolExecutor(int poolSize) {
        mThreadPoolExecutor = new ThreadPoolExecutor(poolSize,
                MAXIMUM_POOL_SIZE, KEEP_ALIVE, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(), sThreadFactory);
    }

    public int getPoolSize() {
        return mThreadPoolExecutor.getCorePoolSize();
    }

    public void setPoolSize(int poolSize) {
        if (poolSize > 0) {
            mThreadPoolExecutor.setCorePoolSize(poolSize);
        }
    }

    public boolean isBusy() {
        return mThreadPoolExecutor.getActiveCount() >= mThreadPoolExecutor
                .getCorePoolSize();
    }

    @Override
    public void execute(final Runnable r) {
        mThreadPoolExecutor.execute(r);
    }
}
