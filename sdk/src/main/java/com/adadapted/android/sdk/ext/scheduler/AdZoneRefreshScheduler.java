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
        void onAdZoneRefreshTimer(final Ad ad);
    }

    public AdZoneRefreshScheduler(final Listener listener) {
        mListener = listener;
    }

    public void schedule(final Ad ad) {
        if(ad == null || ad.getRefreshTimeInMs() <= 0) { return; }

        final long interval = ad.getRefreshTimeInMs();

        this.schedule(new TimerTask() {

            @Override
            public void run() {
                notifyAdZoneRefreshTimer(ad);
            }

        }, interval);
    }

    private void notifyAdZoneRefreshTimer(final Ad ad) {
        mListener.onAdZoneRefreshTimer(ad);
    }
}
