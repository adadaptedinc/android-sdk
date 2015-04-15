package com.adadapted.android.sdk.ext.scheduler;

import com.adadapted.android.sdk.core.ad.Ad;

import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by chrisweeden on 3/23/15.
 */
public class AdZoneRefreshScheduler extends Timer {
    private final Set<Listener> listeners;

    public interface Listener {
        void onAdZoneRefreshTimer();
    }

    public AdZoneRefreshScheduler() {
        listeners = new HashSet<>();
    }

    public void schedule(Ad ad) {
        int interval = ad.getRefreshTime() * 1000;

        this.schedule(new TimerTask() {

            @Override
            public void run() {
                notifyAdZoneRefreshTimer();
            }

        }, interval);
    }

    public void addListener(Listener listener) {
        listeners.add(listener);
    }

    public void removeListener(Listener listener) {
        listeners.remove(listener);
    }

    private void notifyAdZoneRefreshTimer() {
        for(Listener listener : listeners) {
            listener.onAdZoneRefreshTimer();
        }
    }
}
