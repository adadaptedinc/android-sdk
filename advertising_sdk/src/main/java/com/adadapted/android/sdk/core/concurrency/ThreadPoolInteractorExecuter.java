package com.adadapted.android.sdk.core.concurrency;

import android.os.Handler;
import android.os.Looper;
import android.os.Process;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolInteractorExecuter {
    private static final String LOGTAG = ThreadPoolInteractorExecuter.class.getName();

    private static ThreadPoolInteractorExecuter instance;

    public static ThreadPoolInteractorExecuter getInstance() {
        if(instance == null) {
            instance = new ThreadPoolInteractorExecuter();
        }

        return instance;
    }

    private final ThreadPoolExecutor pool;
    private final InteractorMainThread mainThread;

    private ThreadPoolInteractorExecuter() {
        final ThreadFactory backgroundPriorityThreadFactory = new InteractorBackgroundThreadFactory();

        this.pool = new ThreadPoolExecutor(
                2,
                2,
                60L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(),
                backgroundPriorityThreadFactory);

        mainThread = new InteractorMainThread();
    }

    public void executeInBackground(@NonNull final Runnable runnable) {
        pool.execute(runnable);
    }

    public void executeOnMain(@NonNull final Runnable runnable) {
        mainThread.execute(runnable);
    }

    private static class InteractorMainThread implements Executor {
        private final Handler handler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(@NonNull final Runnable runnable) {
            handler.post(runnable);
        }
    }

    private static class InteractorBackgroundThreadFactory implements ThreadFactory {
        @Override
        public Thread newThread(@NonNull final Runnable r) {
            Runnable runnableWrapper = new Runnable() {
                @Override
                public void run() {
                    try {
                        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                    } catch (Throwable t) {
                        Log.w(LOGTAG, "Problem setting background thread.", t);
                    }

                    r.run();
                }
            };

            return new Thread(runnableWrapper);
        }
    }
}
