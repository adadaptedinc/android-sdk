package com.adadapted.android.sdk.ext.scheduler;

import android.util.Log;

import com.adadapted.android.sdk.core.session.model.Session;
import com.adadapted.android.sdk.ext.management.SessionManager;

import java.util.Timer;
import java.util.TimerTask;

public class AdRefreshScheduler extends Timer {
    private static final String LOGTAG = AdRefreshScheduler.class.getName();

    public AdRefreshScheduler() {}

    public void schedule(final Session session) {
        if(session == null || session.getPollingInterval() <= 0L) { return; }

        final long interval = session.getPollingInterval();

        this.schedule(new TimerTask() {

            @Override
            public void run() {
                if(session.hasExpired()) {
                    Log.i(LOGTAG, "Session has expired. Expires at: " + session.getExpiresAt().toString());
                    SessionManager.reinitializeSession();
                }
                else {
                    SessionManager.refreshAds();
                }
            }

        }, interval);
    }
}
