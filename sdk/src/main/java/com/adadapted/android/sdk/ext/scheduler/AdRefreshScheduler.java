package com.adadapted.android.sdk.ext.scheduler;

import android.util.Log;

import com.adadapted.android.sdk.AdAdapted;
import com.adadapted.android.sdk.core.ad.AdFetcher;
import com.adadapted.android.sdk.core.device.DeviceInfo;
import com.adadapted.android.sdk.core.event.EventTracker;
import com.adadapted.android.sdk.core.session.Session;
import com.adadapted.android.sdk.core.session.SessionManager;

import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by chrisweeden on 4/15/15.
 */
public class AdRefreshScheduler extends Timer {
    private static final String TAG = AdRefreshScheduler.class.getName();

    private Set<Listener> listeners;

    public interface Listener {
        void onAdRefreshTimer();
    }

    private final SessionManager sessionManager;
    private final EventTracker eventTracker;
    private final AdFetcher adFetcher;

    public AdRefreshScheduler(SessionManager sessionManager,
                              EventTracker eventTracker,
                              AdFetcher adFetcher) {
        listeners = new HashSet<>();

        this.sessionManager = sessionManager;
        this.eventTracker = eventTracker;
        this.adFetcher = adFetcher;
    }

    public void schedule(long interval, final Session session, final DeviceInfo deviceInfo) {
        this.schedule(new TimerTask() {

            @Override
            public void run() {
                Log.d(TAG, "AdRefreshScheduler fired.");

                eventTracker.publishEvents();

                if(session.hasExpired()) {
                    Log.d(TAG, "Session has expired.");
                    sessionManager.reinitialize(deviceInfo);
                }
                else {
                    Log.d(TAG, "Session has NOT expired.");
                    adFetcher.fetchAdsFor(deviceInfo, session);
                }
            }

        }, interval);
    }
}
