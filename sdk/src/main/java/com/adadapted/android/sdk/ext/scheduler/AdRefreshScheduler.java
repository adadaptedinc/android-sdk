package com.adadapted.android.sdk.ext.scheduler;

import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by chrisweeden on 4/15/15.
 */
public class AdRefreshScheduler extends Timer {
    private Set<Listener> listeners;

    public AdRefreshScheduler() {
        listeners = new HashSet<>();
    }

    public interface Listener {
        void onAdRefreshTimer();
    }
    public void schedule(long interval) {
        this.schedule(new TimerTask() {

            @Override
            public void run() {
                notifyAdRefreshTimer();
            }

        }, interval);
    }

    public void addListener(Listener listener) {
        listeners.add(listener);
    }

    public void removeListener(Listener listener) {
        listeners.remove(listener);
    }

    private void notifyAdRefreshTimer() {
        for(Listener listener : listeners) {
            listener.onAdRefreshTimer();
        }
    }
}
