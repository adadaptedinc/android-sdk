package com.adadapted.android.sdk.ext.scheduler;

import com.adadapted.android.sdk.core.ad.model.Ad;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by chrisweeden on 3/23/15.
 */
public class AdZoneRefreshScheduler extends Timer {
    private static final String LOGTAG = AdZoneRefreshScheduler.class.getName();

    private final Listener mListener;

    public interface Listener {
        void onAdZoneRefreshTimer(Ad ad);
    }

    public AdZoneRefreshScheduler(Listener listener) {
        mListener = listener;
    }

    public void schedule(final Ad ad) {
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
        mListener.onAdZoneRefreshTimer(ad);
    }
}
