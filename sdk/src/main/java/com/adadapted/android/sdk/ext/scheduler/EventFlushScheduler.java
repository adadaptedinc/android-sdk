package com.adadapted.android.sdk.ext.scheduler;

import android.content.Context;
import android.os.Handler;

import com.adadapted.android.sdk.config.Config;
import com.adadapted.android.sdk.core.event.EventTracker;
import com.adadapted.android.sdk.core.keywordintercept.KeywordInterceptManager;
import com.adadapted.android.sdk.ext.factory.EventTrackerFactory;
import com.adadapted.android.sdk.ext.factory.KeywordInterceptManagerFactory;

/**
 * Created by chrisweeden on 7/16/15.
 */
public class EventFlushScheduler {
    private final EventTracker eventTracker;
    private final KeywordInterceptManager keywordInterceptManager;
    private final Handler handler;
    private final Runnable runnable;

    private long pollingInterval;

    public EventFlushScheduler(Context context) {
        eventTracker = EventTrackerFactory.getInstance().createEventTracker(context);
        keywordInterceptManager = KeywordInterceptManagerFactory.getInstance().createKeywordInterceptManager(context);

        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                eventTracker.publishEvents();
                keywordInterceptManager.publishEvents();
                handler.postDelayed(this, pollingInterval);
            }


        };
    }

    public void start(long pollingInterval) {
        this.pollingInterval = pollingInterval <= 0L ? Config.DEFAULT_EVENT_POLLING : pollingInterval;
        runnable.run();
    }

    public void stop() {
        handler.removeCallbacks(runnable);
    }
}
