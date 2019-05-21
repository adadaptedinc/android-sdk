package com.adadapted.android.sdk.core.keywordintercept;

import com.adadapted.android.sdk.core.concurrency.ThreadPoolInteractorExecuter;
import com.adadapted.android.sdk.core.session.Session;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class KeywordInterceptClient {
    @SuppressWarnings("unused")
    private static final String LOGTAG = KeywordInterceptClient.class.getName();

    public interface Listener {
        void onKeywordInterceptInitialized(KeywordIntercept keywordIntercept);
    }

    private static KeywordInterceptClient instance;

    public static synchronized void createInstance(final KeywordInterceptAdapter adapter) {
        if(instance == null) {
            instance = new KeywordInterceptClient(adapter);
        }
    }

    private static KeywordInterceptClient getInstance() {
        return instance;
    }

    public static synchronized void initialize(final Session session, final Listener listener) {
        if(instance == null) {
            return;
        }

        ThreadPoolInteractorExecuter.getInstance().executeInBackground(new Runnable() {
            @Override
            public void run() {
                getInstance().performInitialize(session, listener);
            }
        });
    }

    public static synchronized void trackMatched(final Session session,
                                                 final String searchId,
                                                 final String term,
                                                 final String userInput) {
        trackEvent(session, searchId, term, userInput, KeywordInterceptEvent.MATCHED);
    }

    public static synchronized void trackNotMatched(final Session session,
                                                 final String searchId,
                                                 final String userInput) {
        trackEvent(session, searchId, "NA", userInput, KeywordInterceptEvent.NOT_MATCHED);
    }

    public static synchronized void trackPresented(final Session session,
                                                   final String searchId,
                                                   final String term,
                                                   final String userInput) {
        trackEvent(session, searchId, term, userInput, KeywordInterceptEvent.PRESENTED);
    }

    public static synchronized void trackSelected(final Session session,
                                                  final String searchId,
                                                  final String term,
                                                  final String userInput) {
        trackEvent(session, searchId, term, userInput, KeywordInterceptEvent.SELECTED);
    }

    private static synchronized void trackEvent(final Session session,
                                                final String searchId,
                                                final String term,
                                                final String userInput,
                                                final String eventType) {
        if(instance == null) {
            return;
        }

        final String appId = session.getDeviceInfo().getAppId();
        final String sessionId = session.getId();
        final String udid = session.getDeviceInfo().getUdid();
        final String sdkVersion = session.getDeviceInfo().getSdkVersion();

        final KeywordInterceptEvent event = new KeywordInterceptEvent(appId, sessionId, udid, searchId,
                eventType, userInput, term, sdkVersion);

        ThreadPoolInteractorExecuter.getInstance().executeInBackground(new Runnable() {
            @Override
            public void run() {
                getInstance().fileEvent(event);
            }
        });
    }

    public static synchronized void publishEvents() {
        ThreadPoolInteractorExecuter.getInstance().executeInBackground(new Runnable() {
            @Override
            public void run() {
                getInstance().performPublishEvents();
            }
        });
    }

    private final KeywordInterceptAdapter adapter;

    private final Set<KeywordInterceptEvent> events;
    private final Lock eventLock = new ReentrantLock();

    private KeywordInterceptClient(final KeywordInterceptAdapter adapter) {
        this.adapter = adapter;

        this.events = new HashSet<>();
    }

    private void performInitialize(final Session session,
                                   final Listener listener) {
        if(session == null || listener == null) {
            return;
        }

        adapter.init(session, new KeywordInterceptAdapter.Callback() {
            @Override
            public void onSuccess(final KeywordIntercept keywordIntercept) {
                if (keywordIntercept != null) {
                    listener.onKeywordInterceptInitialized(keywordIntercept);
                }
            }

        });
    }

    private void fileEvent(final KeywordInterceptEvent event) {
        eventLock.lock();
        try {
            final Set<KeywordInterceptEvent> currentEvents = new HashSet<>(this.events);
            this.events.clear();

            final Set<KeywordInterceptEvent> resultingEvents = consolidateEvents(event, currentEvents);

            this.events.addAll(resultingEvents);
        }
        finally {
            eventLock.unlock();
        }
    }

    private Set<KeywordInterceptEvent> consolidateEvents(final KeywordInterceptEvent event,
                                                         final Set<KeywordInterceptEvent> events) {
        final Set<KeywordInterceptEvent> resultingEvents = new HashSet<>(this.events);

        // Create a new Set of Events not superseded by the current Event
        for (final KeywordInterceptEvent e : events) {
            if (!event.supersedes(e)) {
                resultingEvents.add(e);
            }
        }

        resultingEvents.add(event);

        return resultingEvents;
    }

    private void performPublishEvents() {
        eventLock.lock();
        try {
            if (events.isEmpty()) {
                return;
            }

            final Set<KeywordInterceptEvent> currentEvents = new HashSet<>(this.events);
            this.events.clear();

            adapter.sendBatch(currentEvents);
        }
        finally {
            eventLock.unlock();
        }
    }
}
