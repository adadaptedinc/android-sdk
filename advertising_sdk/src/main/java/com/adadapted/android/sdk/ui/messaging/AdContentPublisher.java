package com.adadapted.android.sdk.ui.messaging;

import com.adadapted.android.sdk.ui.model.AdContent;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class AdContentPublisher {
    private static AdContentPublisher sAdContentPublisher;

    public static synchronized AdContentPublisher getInstance() {
        if(sAdContentPublisher == null) {
            sAdContentPublisher = new AdContentPublisher();
        }

        return sAdContentPublisher;
    }

    private final Set<AdContentListener> listeners;
    private final Lock lock = new ReentrantLock();

    private AdContentPublisher() {
        listeners = new HashSet<>();
    }

    public void addListener(final AdContentListener listener) {
        lock.lock();
        try {
            if (listener != null) {
                listeners.add(listener);
            }
        }
        finally {
            lock.unlock();
        }
    }

    public void removeListener(final AdContentListener listener) {
        lock.lock();
        try {
            if(listener != null) {
                listeners.remove(listener);
            }
        }
        finally {
            lock.unlock();
        }
    }

    public void publishContent(final String zoneId,
                               final AdContent content) {
        lock.lock();
        try {
            for(final AdContentListener listener : listeners) {
                listener.onContentAvailable(zoneId, content);
            }
        }
        finally {
            lock.unlock();
        }
    }
}
