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

    public static AdEventClient createInstance(final AdEventSink adEventSink) {
        if(instance == null) {
            instance = new AdEventClient(adEventSink);
        }

        return instance;
    }

    private static AdEventClient getInstance() {
        return instance;
    }

    public static synchronized void trackImpression(final Ad ad) {
        ThreadPoolInteractorExecuter.getInstance().executeInBackground(new Runnable() {
            @Override
            public void run() {
                getInstance().fileEvent(ad, AdEvent.Types.IMPRESSION);
            }
        });
    }

    public static void addListener(final Listener listener) {

    }

    public static void removeListener(final Listener listener) {

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

    private final Set<AdEvent> events;
    private final Lock lock = new ReentrantLock();

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
        lock.lock();
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
            lock.unlock();
        }
    }

    private void publishEvents() {
        if(session == null || events.isEmpty()) {
            return;
        }

        final Set<AdEvent> currentEvents = new HashSet<>();

        lock.lock();
        try {
            Log.i(LOGTAG, "Publishing " + events.size() + " events");
            currentEvents.addAll(events);
            events.clear();

            adEventSink.sendBatch(currentEvents);
        }
        finally {
            lock.unlock();
        }
    }

    private void notifyAdEventTracked(final AdEvent event) {
        for(Listener l : listeners) {
            l.onAdEventTracked(event);
        }
    }

    @Override
    public void onSessionAvailable(Session session) {
        lock.lock();
        try {
            this.session = session;
            startPublishTimer();
        }
        finally {
            lock.unlock();
        }
    }

    @Override
    public void onAdsAvailable(Session session) {
        lock.lock();
        try {
            this.session = session;
        }
        finally {
            lock.unlock();
        }
    }

    @Override
    public void onSessionInitFailed() {}
}
