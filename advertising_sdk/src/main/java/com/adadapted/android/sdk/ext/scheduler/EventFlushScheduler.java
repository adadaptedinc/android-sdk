package com.adadapted.android.sdk.ext.scheduler;

import android.os.Handler;
import android.util.Log;

import com.adadapted.android.sdk.config.Config;
import com.adadapted.android.sdk.ext.management.AdEventTrackingManager;
import com.adadapted.android.sdk.ext.management.KeywordInterceptEventTrackingManager;

/**
 * Created by chrisweeden on 7/16/15.
 */
public class EventFlushScheduler {
    private static final String LOGTAG = EventFlushScheduler.class.getName();

    private final Handler mHandler;
    private final Runnable mRunnable;

    private long pollingInterval;

    public EventFlushScheduler() {
        mHandler = new Handler();
        mRunnable = new Runnable() {
            @Override
            public void run() {
                flushEvents();
                mHandler.postDelayed(this, pollingInterval);
            }
        };
    }

    private void flushEvents() {
        AdEventTrackingManager.publish();
        KeywordInterceptEventTrackingManager.publish();
    }

    public void start(final long pollingInterval) {
        Log.i(LOGTAG, "Starting up Event Publisher.");
        this.pollingInterval = pollingInterval <= 0L ? Config.DEFAULT_EVENT_POLLING : pollingInterval;
        mRunnable.run();
    }

    public void stop() {
        Log.i(LOGTAG, "Shutting down Event Publisher.");
        flushEvents();
        mHandler.removeCallbacks(mRunnable);
    }
}
