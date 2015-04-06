package com.adadapted.android.sdk;

import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by chrisweeden on 3/23/15.
 */
class AdZoneRefreshScheduler extends Timer {
    private final Set<Listener> listeners;

    static interface Listener {
        public void onAdZoneRefreshTimer();
    }

    AdZoneRefreshScheduler() {
        listeners = new HashSet<>();
    }

    void schedule(Ad ad) {
        int interval = ad.getRefreshTime() * 1000;

        this.schedule(new TimerTask() {

            @Override
            public void run() {
                notifyAdZoneRefreshTimer();
            }

        }, interval);
    }

    void addListener(AdZoneRefreshScheduler.Listener listener) {
        listeners.add(listener);
    }

    void removeListener(AdZoneRefreshScheduler.Listener listener) {
        listeners.remove(listener);
    }

    private void notifyAdZoneRefreshTimer() {
        for(AdZoneRefreshScheduler.Listener listener : listeners) {
            listener.onAdZoneRefreshTimer();
        }
    }
}
