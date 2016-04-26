package com.adadapted.android.sdk.ext.scheduler;

import android.os.Handler;

import com.adadapted.android.sdk.config.Config;
import com.adadapted.android.sdk.core.device.model.DeviceInfo;
import com.adadapted.android.sdk.core.event.EventTracker;
import com.adadapted.android.sdk.core.keywordintercept.KeywordInterceptManager;
import com.adadapted.android.sdk.core.session.model.Session;
import com.adadapted.android.sdk.ext.factory.EventTrackerFactory;
import com.adadapted.android.sdk.ext.factory.KeywordInterceptManagerFactory;

/**
 * Created by chrisweeden on 7/16/15.
 */
public class EventFlushScheduler {
    private final EventTracker mEventTracker;
    private final KeywordInterceptManager mKiManager;
    private final Handler mHandler;
    private final Runnable mRunnable;

    private long pollingInterval;

    public EventFlushScheduler(Session session) {
        DeviceInfo deviceInfo = session.getDeviceInfo();

        mEventTracker = EventTrackerFactory.createEventTracker(deviceInfo);
        mKiManager = KeywordInterceptManagerFactory.createKeywordInterceptManager(deviceInfo);

        mHandler = new Handler();
        mRunnable = new Runnable() {
            @Override
            public void run() {
                mEventTracker.publishEvents();
                mKiManager.publishEvents();
                mHandler.postDelayed(this, pollingInterval);
            }


        };
    }

    public void start(long pollingInterval) {
        this.pollingInterval = pollingInterval <= 0L ? Config.DEFAULT_EVENT_POLLING : pollingInterval;
        mRunnable.run();
    }

    public void stop() {
        mHandler.removeCallbacks(mRunnable);
    }
}
