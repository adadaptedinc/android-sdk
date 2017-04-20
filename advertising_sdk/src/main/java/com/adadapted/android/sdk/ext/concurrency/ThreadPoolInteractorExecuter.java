package com.adadapted.android.sdk.ext.concurrency;

import android.os.Handler;
import android.os.Looper;
import android.os.Process;
import android.support.annotation.NonNull;
import android.util.Log;

import com.adadapted.android.sdk.core.common.Interactor;
import com.adadapted.android.sdk.core.common.InteractorExecuter;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by chrisweeden on 9/26/16.
 */
public class ThreadPoolInteractorExecuter implements InteractorExecuter {
    private static final String LOGTAG = ThreadPoolInteractorExecuter.class.getName();

    private static ThreadPoolInteractorExecuter instance;

    public static ThreadPoolInteractorExecuter getInstance() {
        if(instance == null) {
            instance = new ThreadPoolInteractorExecuter();
        }

        return instance;
    }

    //private static final int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();

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

    @Override
    public void executeInBackground(final Interactor interactor) {
        pool.execute(new Runnable() {
            @Override
            public void run() {
                interactor.execute();
            }
        });
    }

    @Override
    public void executeOnMain(final Interactor interactor) {
        mainThread.execute(new Runnable() {
            @Override
            public void run() {
                interactor.execute();
            }
        });
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
