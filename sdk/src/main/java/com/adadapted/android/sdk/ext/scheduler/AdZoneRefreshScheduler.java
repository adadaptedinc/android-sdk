package com.adadapted.android.sdk.ext.scheduler;

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
        void onAdZoneRefreshTimer();
    }

    public AdZoneRefreshScheduler(Listener listener) {
        this.listener = listener;
    }

    public void schedule(Ad ad) {
        long interval = ad.getRefreshTimeInMs();
        if(interval <= 0) { return; }

        this.schedule(new TimerTask() {

            @Override
            public void run() {
                notifyAdZoneRefreshTimer();
            }

        }, interval);
    }

    private void notifyAdZoneRefreshTimer() {
        listener.onAdZoneRefreshTimer();
    }
}
