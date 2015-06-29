package com.adadapted.android.sdk.ext.scheduler;

import android.content.Context;
import android.util.Log;

import com.adadapted.android.sdk.core.ad.AdFetcher;
import com.adadapted.android.sdk.core.device.model.DeviceInfo;
import com.adadapted.android.sdk.core.event.EventTracker;
import com.adadapted.android.sdk.core.session.model.Session;
import com.adadapted.android.sdk.core.session.SessionManager;
import com.adadapted.android.sdk.ext.factory.AdFetcherFactory;
import com.adadapted.android.sdk.ext.factory.EventTrackerFactory;
import com.adadapted.android.sdk.ext.factory.SessionManagerFactory;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by chrisweeden on 4/15/15.
 */
public class AdRefreshScheduler extends Timer {
    private static final String TAG = AdRefreshScheduler.class.getName();

    private final SessionManager sessionManager;
    private final EventTracker eventTracker;
    private final AdFetcher adFetcher;

    public AdRefreshScheduler(Context context) {
        this.sessionManager = SessionManagerFactory.getInstance(context).createSessionManager();
        this.eventTracker = EventTrackerFactory.getInstance(context).createEventTracker();
        this.adFetcher = AdFetcherFactory.getInstance(context).createAdFetcher();
    }

    public void schedule(long interval, final Session session, final DeviceInfo deviceInfo) {
        Log.d(TAG, "Scheduling next Ad refresh.");
        if(interval <= 0L) { return; }

        this.schedule(new TimerTask() {

            @Override
            public void run() {
                Log.d(TAG, "AdRefreshScheduler fired.");

                eventTracker.publishEvents();

                if(session.hasExpired()) {
                    Log.d(TAG, "Session has expired.");
                    sessionManager.reinitialize(deviceInfo, session);
                }
                else {
                    Log.d(TAG, "Session has NOT expired.");
                    adFetcher.fetchAdsFor(deviceInfo, session);
                }
            }

        }, interval);
    }
}
