package com.adadapted.android.sdk.ext.scheduler;

import com.adadapted.android.sdk.core.ad.model.Ad;

import java.util.Timer;
import java.util.TimerTask;

public class AdZoneRefreshScheduler extends Timer {
    @SuppressWarnings("unused")
    private static final String LOGTAG = AdZoneRefreshScheduler.class.getName();

    private Listener mListener;
    private Ad nextAd;

    public interface Listener {
        void onAdZoneRefreshTimer(final Ad ad);
    }

    public AdZoneRefreshScheduler() {}

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

    public void setListener(final Listener listener) {
        mListener = listener;

        if(nextAd != null) {
            notifyAdZoneRefreshTimer(nextAd);
            nextAd = null;
        }
    }

    public void removeListener() {
        mListener = null;
    }

    private void notifyAdZoneRefreshTimer(final Ad ad) {
        if(mListener != null) {
            nextAd = null;
            mListener.onAdZoneRefreshTimer(ad);
        }
        else {
            nextAd = ad;
        }
    }
}
