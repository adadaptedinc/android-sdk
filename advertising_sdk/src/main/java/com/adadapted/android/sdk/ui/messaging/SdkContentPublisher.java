package com.adadapted.android.sdk.ui.messaging;

import com.adadapted.android.sdk.ui.model.AdContentPayload;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SdkContentPublisher {
    private static SdkContentPublisher sSdkContentPublisher;

    public static synchronized SdkContentPublisher getInstance() {
        if(sSdkContentPublisher == null) {
            sSdkContentPublisher = new SdkContentPublisher();
        }

        return sSdkContentPublisher;
    }

    private final Set<AaSdkContentListener> listeners;
    private final Lock lock = new ReentrantLock();

    private SdkContentPublisher() {
        listeners = new HashSet<>();
    }

    public void addListener(final AaSdkContentListener listener) {
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

    public void removeListener(final AaSdkContentListener listener) {
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
                               final AdContentPayload payload) {
        lock.lock();
        try {
            for(final AaSdkContentListener listener : listeners) {
                listener.onContentAvailable(zoneId, payload);
            }
        }
        finally {
            lock.unlock();
        }
    }
}
