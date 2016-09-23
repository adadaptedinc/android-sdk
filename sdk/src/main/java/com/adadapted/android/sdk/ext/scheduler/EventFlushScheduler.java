package com.adadapted.android.sdk.ext.scheduler;

import android.os.Handler;
import android.util.Log;

import com.adadapted.android.sdk.config.Config;
import com.adadapted.android.sdk.core.device.model.DeviceInfo;
import com.adadapted.android.sdk.core.event.AdEventTracker;
import com.adadapted.android.sdk.core.keywordintercept.KeywordInterceptManager;
import com.adadapted.android.sdk.core.session.model.Session;
import com.adadapted.android.sdk.ext.factory.AdEventTrackerFactory;
import com.adadapted.android.sdk.ext.factory.AnomalyTrackerFactory;
import com.adadapted.android.sdk.ext.factory.AppEventTrackerFactory;
import com.adadapted.android.sdk.ext.factory.KeywordInterceptManagerFactory;

/**
 * Created by chrisweeden on 7/16/15.
 */
public class EventFlushScheduler {
    private static final String LOGTAG = EventFlushScheduler.class.getName();

    private final AdEventTracker mEventTracker;
    private final KeywordInterceptManager mKiManager;
    private final Handler mHandler;
    private final Runnable mRunnable;

    private long pollingInterval;

    public EventFlushScheduler(final Session session) {
        final DeviceInfo deviceInfo = session.getDeviceInfo();

        mEventTracker = AdEventTrackerFactory.createEventTracker(deviceInfo);
        mKiManager = KeywordInterceptManagerFactory.createKeywordInterceptManager(deviceInfo);

        flushEvents();

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
        mEventTracker.publishEvents();
        mKiManager.publishEvents();
        AppEventTrackerFactory.publishEvents();
        AnomalyTrackerFactory.publishEvents();
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
