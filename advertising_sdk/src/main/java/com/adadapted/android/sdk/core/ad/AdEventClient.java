package com.adadapted.android.sdk.core.ad;

import com.adadapted.android.sdk.core.concurrency.ThreadPoolInteractorExecuter;
import com.adadapted.android.sdk.core.session.Session;
import com.adadapted.android.sdk.core.session.SessionClient;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class AdEventClient implements SessionClient.Listener {
    @SuppressWarnings("unused")
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
                final int count = ImpressionIdCounter.getInstance().getIncrementedCountFor(ad.getImpressionId());
                getInstance().fileEvent(ad, AdEvent.Types.IMPRESSION, count);
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
                final int count = ImpressionIdCounter.getInstance().getCurrentCountFor(ad.getImpressionId());
                getInstance().fileEvent(ad, AdEvent.Types.IMPRESSION_END, count);
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
                final int count = ImpressionIdCounter.getInstance().getCurrentCountFor(ad.getImpressionId());
                getInstance().fileEvent(ad, AdEvent.Types.INTERACTION, count);
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
                final int count = ImpressionIdCounter.getInstance().getCurrentCountFor(ad.getImpressionId());
                getInstance().fileEvent(ad, AdEvent.Types.POPUP_BEGIN, count);
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
                final int count = ImpressionIdCounter.getInstance().getCurrentCountFor(ad.getImpressionId());
                getInstance().fileEvent(ad, AdEvent.Types.POPUP_END, count);
            }
        });
    }

    public static synchronized void publishEvents() {
        if(instance == null) {
            return;
        }

        ThreadPoolInteractorExecuter.getInstance().executeInBackground(new Runnable() {
            @Override
            public void run() {
                getInstance().performPublishEvents();
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

    private void fileEvent(final Ad ad, final String eventType, final int count) {
        if(session == null) {
            return;
        }

        eventLock.lock();
        try {
            final AdEvent event = new AdEvent(
                ad.getId(),
                ad.getZoneId(),
                ad.getImpressionId() + "::" + count,
                eventType
            );

            events.add(event);
            notifyAdEventTracked(event);
        }
        finally {
            eventLock.unlock();
        }
    }

    private void performPublishEvents() {
        if(session == null || events.isEmpty()) {
            return;
        }

        eventLock.lock();
        try {
            final Set<AdEvent> currentEvents = new HashSet<>(events);
            events.clear();

            adEventSink.sendBatch(session, currentEvents);
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
            for(final Listener l : listeners) {
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
