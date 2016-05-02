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
    private static final String LOGTAG = AdRefreshScheduler.class.getName();

    private final SessionManager mSessionManager;
    private final KeywordInterceptManager mKiManager;
    private final EventTracker mEventTracker;
    private final AdFetcher mAdFetcher;

    public AdRefreshScheduler(Context context, Session session) {
        final DeviceInfo deviceInfo = session.getDeviceInfo();

        mSessionManager = SessionManagerFactory.createSessionManager(context);
        mKiManager = KeywordInterceptManagerFactory.createKeywordInterceptManager(deviceInfo);
        mEventTracker = EventTrackerFactory.createEventTracker(deviceInfo);
        mAdFetcher = AdFetcherFactory.createAdFetcher(deviceInfo);
    }

    public void schedule(final Session session) {
        if(session == null || session.getPollingInterval() <= 0L) { return; }

        final long interval = session.getPollingInterval();

        this.schedule(new TimerTask() {

            @Override
            public void run() {
                mEventTracker.publishEvents();
                mKiManager.publishEvents();

                if(session.hasExpired()) {
                    Log.i(LOGTAG, "Session has expired.");
                    mSessionManager.initialize(session.getDeviceInfo());
                }
                else {
                    mAdFetcher.fetchAdsFor(session);
                }
            }

        }, interval);
    }
}
