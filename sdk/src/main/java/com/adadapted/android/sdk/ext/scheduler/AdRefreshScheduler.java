package com.adadapted.android.sdk.ext.scheduler;

import android.content.Context;
import android.util.Log;

import com.adadapted.android.sdk.core.ad.AdFetcher;
import com.adadapted.android.sdk.core.device.model.DeviceInfo;
import com.adadapted.android.sdk.core.event.EventTracker;
import com.adadapted.android.sdk.core.keywordintercept.KeywordInterceptManager;
import com.adadapted.android.sdk.core.session.model.Session;
import com.adadapted.android.sdk.core.session.SessionManager;
import com.adadapted.android.sdk.ext.factory.AdFetcherFactory;
import com.adadapted.android.sdk.ext.factory.EventTrackerFactory;
import com.adadapted.android.sdk.ext.factory.KeywordInterceptManagerFactory;
import com.adadapted.android.sdk.ext.factory.SessionManagerFactory;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by chrisweeden on 4/15/15.
 */
public class AdRefreshScheduler extends Timer {
    private static final String TAG = AdRefreshScheduler.class.getName();

    private final SessionManager sessionManager;
    private final KeywordInterceptManager keywordInterceptManager;
    private final EventTracker eventTracker;
    private final AdFetcher adFetcher;

    public AdRefreshScheduler(Context context) {
        sessionManager = SessionManagerFactory.getInstance(context).createSessionManager();
        keywordInterceptManager = KeywordInterceptManagerFactory.getInstance(context).createKeywordInterceptManager();
        eventTracker = EventTrackerFactory.getInstance(context).createEventTracker();
        adFetcher = AdFetcherFactory.getInstance(context).createAdFetcher();
    }

    public void schedule(final Session session, final DeviceInfo deviceInfo) {
        long interval = session.getPollingInterval();
        if(interval <= 0L) { return; }

        this.schedule(new TimerTask() {

            @Override
            public void run() {
                eventTracker.publishEvents();
                keywordInterceptManager.publishEvents();

                if(session.hasExpired()) {
                    Log.i(TAG, "Session has expired.");
                    sessionManager.reinitialize(deviceInfo, session);
                }
                else {
                    adFetcher.fetchAdsFor(deviceInfo, session);
                }
            }

        }, interval);
    }
}
