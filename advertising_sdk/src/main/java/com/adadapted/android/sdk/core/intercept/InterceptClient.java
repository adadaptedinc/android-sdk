package com.adadapted.android.sdk.core.intercept;

import com.adadapted.android.sdk.core.concurrency.ThreadPoolInteractorExecuter;
import com.adadapted.android.sdk.core.device.DeviceInfo;
import com.adadapted.android.sdk.core.session.Session;
import com.adadapted.android.sdk.core.session.SessionClient;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class InterceptClient implements SessionClient.Listener {
    @SuppressWarnings("unused")
    private static final String LOGTAG = InterceptClient.class.getName();

    public interface Listener {
        void onKeywordInterceptInitialized(Intercept intercept);
    }

    private static InterceptClient instance;

    public static synchronized void createInstance(final InterceptAdapter adapter) {
        if(instance == null) {
            instance = new InterceptClient(adapter);
        }
    }

    private static InterceptClient getInstance() {
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

    public static synchronized void trackMatched(final String searchId,
                                                 final String termId,
                                                 final String term,
                                                 final String userInput) {
        trackEvent(searchId, termId, term, userInput, InterceptEvent.MATCHED);
    }

    public static synchronized void trackPresented(final String searchId,
                                                   final String termId,
                                                   final String term,
                                                   final String userInput) {
        trackEvent(searchId, termId, term, userInput, InterceptEvent.PRESENTED);
    }

    public static synchronized void trackSelected(final String searchId,
                                                  final String termId,
                                                  final String term,
                                                  final String userInput) {
        trackEvent(searchId, termId, term, userInput, InterceptEvent.SELECTED);
    }

    public static synchronized void trackNotMatched(final String searchId,
                                                    final String userInput) {
        trackEvent(searchId, "", "NA", userInput, InterceptEvent.NOT_MATCHED);
    }

    private static synchronized void trackEvent(final String searchId,
                                                final String termId,
                                                final String term,
                                                final String userInput,
                                                final String eventType) {
        if(instance == null) {
            return;
        }

        final InterceptEvent event = new InterceptEvent(
            searchId,
            eventType,
            userInput,
            termId,
            term
        );

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

    private final InterceptAdapter adapter;

    private final Set<InterceptEvent> events;
    private final Lock eventLock = new ReentrantLock();

    private Session currentSession;

    private InterceptClient(final InterceptAdapter adapter) {
        this.adapter = adapter;

        SessionClient.addListener(this);
        this.events = new HashSet<>();
    }

    private void performInitialize(final Session session,
                                   final Listener listener) {
        if(session == null || listener == null) {
            return;
        }

        adapter.retrieve(session, new InterceptAdapter.Callback() {
            @Override
            public void onSuccess(final Intercept intercept) {
                if (intercept != null) {
                    listener.onKeywordInterceptInitialized(intercept);
                }
            }
        });
    }

    private void fileEvent(final InterceptEvent event) {
        eventLock.lock();
        try {
            final Set<InterceptEvent> currentEvents = new HashSet<>(this.events);
            this.events.clear();

            final Set<InterceptEvent> resultingEvents = consolidateEvents(event, currentEvents);
            this.events.addAll(resultingEvents);
        }
        finally {
            eventLock.unlock();
        }
    }

    private Set<InterceptEvent> consolidateEvents(final InterceptEvent event,
                                                  final Set<InterceptEvent> events) {
        final Set<InterceptEvent> resultingEvents = new HashSet<>(this.events);

        // Create a new Set of Events not superseded by the current Event
        for (final InterceptEvent e : events) {
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

            final Set<InterceptEvent> currentEvents = new HashSet<>(this.events);
            this.events.clear();

            adapter.sendEvents(currentSession, currentEvents);
        }
        finally {
            eventLock.unlock();
        }
    }

    @Override
    public void onSessionAvailable(final Session session) {
        eventLock.lock();
        try {
            currentSession = session;
        }
        finally {
            eventLock.unlock();
        }
    }

    @Override
    public void onAdsAvailable(final Session session) { }

    @Override
    public void onSessionInitFailed() { }
}
