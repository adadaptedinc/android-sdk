package com.adadapted.android.sdk.core.ad;

import android.os.Handler;
import android.util.Log;

import com.adadapted.android.sdk.config.Config;
import com.adadapted.android.sdk.core.concurrency.ThreadPoolInteractorExecuter;
import com.adadapted.android.sdk.core.session.Session;
import com.adadapted.android.sdk.core.session.SessionClient;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class AdEventClient implements SessionClient.Listener {
    private static final String LOGTAG = AdEventClient.class.getName();

    public interface Listener {
        void onAdEventTracked(AdEvent event);
    }

    private static AdEventClient instance;

    public static void createInstance(final AdEventSink adEventSink) {
        if(instance == null) {
            instance = new AdEventClient(adEventSink);
        }
    }

    private static AdEventClient getInstance() {
        return instance;
    }

    public static synchronized void addListener(final Listener listener) {
        if(instance == null) {
            return;
        }

        getInstance().performAddListener(listener);
    }

    public static synchronized void removeListener(final Listener listener) {
        if(instance == null) {
            return;
        }

        getInstance().performRemoveListener(listener);
    }

    public static synchronized void trackImpression(final Ad ad) {
        if(instance == null) {
            return;
        }

        ThreadPoolInteractorExecuter.getInstance().executeInBackground(new Runnable() {
            @Override
            public void run() {
                getInstance().fileEvent(ad, AdEvent.Types.IMPRESSION);
            }
        });
    }

    public static synchronized void trackImpressionEnd(final Ad ad) {
        if(instance == null) {
            return;
        }

        ThreadPoolInteractorExecuter.getInstance().executeInBackground(new Runnable() {
            @Override
            public void run() {
                getInstance().fileEvent(ad, AdEvent.Types.IMPRESSION_END);
            }
        });
    }

    public static synchronized void trackInteraction(final Ad ad) {
        if(instance == null) {
            return;
        }

        ThreadPoolInteractorExecuter.getInstance().executeInBackground(new Runnable() {
            @Override
            public void run() {
                getInstance().fileEvent(ad, AdEvent.Types.INTERACTION);
            }
        });
    }

    public static synchronized void trackPopupBegin(final Ad ad) {
        if(instance == null) {
            return;
        }

        ThreadPoolInteractorExecuter.getInstance().executeInBackground(new Runnable() {
            @Override
            public void run() {
                getInstance().fileEvent(ad, AdEvent.Types.POPUP_BEGIN);
            }
        });
    }

    public static synchronized void trackPopupEnd(final Ad ad) {
        if(instance == null) {
            return;
        }

        ThreadPoolInteractorExecuter.getInstance().executeInBackground(new Runnable() {
            @Override
            public void run() {
                getInstance().fileEvent(ad, AdEvent.Types.POPUP_END);
            }
        });
    }

    private final AdEventSink adEventSink;

    private final Set<Listener> listeners;
    private final Lock listenerLock = new ReentrantLock();

    private final Set<AdEvent> events;
    private final Lock eventLock = new ReentrantLock();

    private Session session;

    private AdEventClient(final AdEventSink adEventSink) {
        this.adEventSink = adEventSink;

        this.events = new HashSet<>();
        this.listeners = new HashSet<>();

        SessionClient.addListener(this);
    }

    private void startPublishTimer() {
        new Runnable() {
            @Override
            public void run() {
                publishEvents();
                new Handler().postDelayed(this, Config.DEFAULT_EVENT_POLLING);
            }
        }.run();
    }

    private void fileEvent(final Ad ad, final String eventType) {
        eventLock.lock();
        try {
            final AdEvent event = new AdEvent(
                session.getDeviceInfo().getAppId(),
                session.getDeviceInfo().getUdid(),
                session.getId(),
                ad.getId(),
                ad.getImpressionId(),
                eventType,
                session.getDeviceInfo().getSdkVersion()
            );

            events.add(event);
            notifyAdEventTracked(event);
        }
        finally {
            eventLock.unlock();
        }
    }

    private void publishEvents() {
        if(session == null || events.isEmpty()) {
            return;
        }

        final Set<AdEvent> currentEvents = new HashSet<>();

        eventLock.lock();
        try {
            Log.i(LOGTAG, "Publishing " + events.size() + " events");
            currentEvents.addAll(events);
            events.clear();

            adEventSink.sendBatch(currentEvents);
        }
        finally {
            eventLock.unlock();
        }
    }

    private void performAddListener(final Listener listener) {
        listenerLock.lock();
        try {
            listeners.add(listener);
        }
        finally {
            listenerLock.unlock();
        }
    }

    private void performRemoveListener(final Listener listener) {
        listenerLock.lock();
        try {
            listeners.remove(listener);
        }
        finally {
            listenerLock.unlock();
        }
    }

    private void notifyAdEventTracked(final AdEvent event) {
        listenerLock.lock();
        try {
            for(Listener l : listeners) {
                l.onAdEventTracked(event);
            }
        }
        finally {
            listenerLock.unlock();
        }
    }

    @Override
    public void onSessionAvailable(Session session) {
        eventLock.lock();
        try {
            this.session = session;
            startPublishTimer();
        }
        finally {
            eventLock.unlock();
        }
    }

    @Override
    public void onAdsAvailable(Session session) {
        eventLock.lock();
        try {
            this.session = session;
        }
        finally {
            eventLock.unlock();
        }
    }

    @Override
    public void onSessionInitFailed() {}
}
