package com.adadapted.android.sdk;

import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by chrisweeden on 3/23/15.
 */
public class AdZoneRefreshScheduler extends Timer {
    private Set<Listener> listeners;

    public static interface Listener {
        public void onAdZoneRefreshTimer();
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

    public void addListener(AdZoneRefreshScheduler.Listener listener) {
        listeners.add(listener);
    }

    public void removeListener(AdZoneRefreshScheduler.Listener listener) {
        listeners.remove(listener);
    }

    public void notifyAdZoneRefreshTimer() {
        for(AdZoneRefreshScheduler.Listener listener : listeners) {
            listener.onAdZoneRefreshTimer();
        }
    }
}
