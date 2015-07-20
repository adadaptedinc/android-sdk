package com.adadapted.android.sdk.ext.scheduler;

import android.util.Log;

import com.adadapted.android.sdk.core.ad.model.Ad;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by chrisweeden on 3/23/15.
 */
public class AdZoneRefreshScheduler extends Timer {
    private static final String TAG = AdZoneRefreshScheduler.class.getName();

    private final Listener listener;

    public interface Listener {
        void onAdZoneRefreshTimer(Ad ad);
    }

    public AdZoneRefreshScheduler(Listener listener) {
        this.listener = listener;
    }

    public void schedule(final Ad ad) {
        Log.d(TAG, "Scheduling Refresh For Ad " + ad.getAdId());

        long interval = ad.getRefreshTimeInMs();
        if(interval <= 0) { return; }

        this.schedule(new TimerTask() {

            @Override
            public void run() {
                notifyAdZoneRefreshTimer(ad);
            }

        }, interval);
    }

    private void notifyAdZoneRefreshTimer(Ad ad) {
        listener.onAdZoneRefreshTimer(ad);
    }
}
