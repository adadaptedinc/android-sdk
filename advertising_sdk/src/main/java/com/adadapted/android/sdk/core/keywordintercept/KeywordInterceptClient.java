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

    public static synchronized KeywordInterceptClient createInstance(final KeywordInterceptAdapter adapter,
                                                                      final KeywordInterceptEventSink sink) {
        if(instance == null) {
            instance = new KeywordInterceptClient(adapter, sink);
        }

        return instance;
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

    public static synchronized void trackMatched(final Session session, final String term, final String userInput) {
        trackEvent(session, "", term, userInput, "matched");
    }

    public static synchronized void trackPresented(final Session session, final String term, final String userInput) {
        trackEvent(session, "", term, userInput, "presented");
    }

    public static synchronized void trackSelected(final Session session, final String term, final String userInput) {
        trackEvent(session, "", term, userInput, "selected");
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

    private final KeywordInterceptAdapter adapter;
    private final KeywordInterceptEventSink sink;

    private final Set<KeywordInterceptEvent> events;
    private final Lock lock = new ReentrantLock();

    private KeywordInterceptClient(final KeywordInterceptAdapter adapter,
                                   final KeywordInterceptEventSink sink) {
        this.adapter = adapter;
        this.sink = sink;

        this.events = new HashSet<>();
    }

    private void performInitialize(final Session session,
                                   final Listener listener) {
        adapter.init(session, new KeywordInterceptAdapter.Callback() {
            @Override
            public void onSuccess(final KeywordIntercept keywordIntercept) {
                listener.onKeywordInterceptInitialized(keywordIntercept);
            }

            @Override
            public void onFailure() {}
        });
    }

    private void fileEvent(final KeywordInterceptEvent event) {
        lock.lock();
        try {
            final Set<KeywordInterceptEvent> events = new HashSet<>(this.events);

            for (final KeywordInterceptEvent e : events) {
                if (event.supercedes(e)) {
                    events.remove(e);
                }
            }

            events.add(event);

            this.events.clear();
            this.events.addAll(events);
        }
        finally {
            lock.unlock();
        }
    }

    public void publishEvents() {
        lock.lock();
        try {
            if (events.isEmpty()) {
                return;
            }

            final Set<KeywordInterceptEvent> events = new HashSet<>(this.events);
            this.events.clear();

            sink.sendBatch(events);
        }
        finally {
            lock.unlock();
        }
    }
}
