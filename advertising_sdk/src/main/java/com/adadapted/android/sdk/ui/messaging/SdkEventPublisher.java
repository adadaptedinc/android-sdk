package com.adadapted.android.sdk.ui.messaging;

import android.os.Handler;
import android.os.Looper;

import com.adadapted.android.sdk.core.ad.AdEvent;
import com.adadapted.android.sdk.core.ad.AdEventClient;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SdkEventPublisher implements AdEventClient.Listener {
    @SuppressWarnings("unused")
    private static final String LOGTAG = SdkEventPublisher.class.getName();

    private static SdkEventPublisher instance;

    public static SdkEventPublisher getInstance() {
        if(instance == null) {
            instance = new SdkEventPublisher();
        }

        return instance;
    }

    private static class EventTypes {
        static final String IMPRESSION = "impression";
        static final String CLICK = "click";
    }

    private AaSdkEventListener listener;
    private final Lock lock = new ReentrantLock();

    private SdkEventPublisher() {
        AdEventClient.Companion.addListener(this);
    }

    public void setListener(final AaSdkEventListener listener) {
        lock.lock();
        try {
            this.listener = listener;
        }
        finally {
            lock.unlock();
        }
    }

    public void unsetListener() {
        lock.lock();
        try {
            listener = null;
        }
        finally {
            lock.unlock();
        }
    }

    @Override
    public void onAdEventTracked(final AdEvent event) {
        if(listener == null || event == null) {
            return;
        }

        lock.lock();
        try {
            if(event.getEventType().equals(AdEvent.Types.IMPRESSION)) {
                notifyNextAdEvent(event.getZoneId(), EventTypes.IMPRESSION);
            }
            else if(event.getEventType().equals(AdEvent.Types.INTERACTION)) {
                notifyNextAdEvent(event.getZoneId(), EventTypes.CLICK);
            }
        }
        finally {
            lock.unlock();
        }
    }

    private void notifyNextAdEvent(final String zoneId, final String eventType) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                listener.onNextAdEvent(zoneId, eventType);
            }
        });
    }
}
