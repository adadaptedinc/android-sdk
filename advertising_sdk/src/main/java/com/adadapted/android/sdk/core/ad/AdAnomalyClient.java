package com.adadapted.android.sdk.core.ad;

import com.adadapted.android.sdk.core.concurrency.ThreadPoolInteractorExecuter;
import com.adadapted.android.sdk.core.session.Session;
import com.adadapted.android.sdk.core.session.SessionClient;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class AdAnomalyClient implements SessionClient.Listener{
    @SuppressWarnings("unused")
    private static final String LOGTAG = AdAnomalyClient.class.getName();

    private static AdAnomalyClient instance;

    public static AdAnomalyClient createInstance(final AdAnomalySink sink) {
        if(instance == null) {
            instance = new AdAnomalyClient(sink);
        }

        return instance;
    }

    public static AdAnomalyClient getInstance() {
        return instance;
    }

    public static synchronized void trackAnomaly(final String adId,
                                                 final String eventPath,
                                                 final String code,
                                                 final String message) {
        if(instance == null) {
            return;
        }

        ThreadPoolInteractorExecuter.getInstance().executeInBackground(new Runnable() {
            @Override
            public void run() {
                getInstance().performTrackAnomaly(adId, eventPath, code, message);
            }
        });
    }

    private final AdAnomalySink sink;

    private Session session;
    private final Lock lock = new ReentrantLock();

    private AdAnomalyClient(final AdAnomalySink sink) {
        this.sink = sink;

        SessionClient.addListener(this);
    }

    private void performTrackAnomaly(final String adId,
                                     final String eventPath,
                                     final String code,
                                     final String message) {
        sink.sendBatch(session, adId, eventPath, code, message);
    }

    @Override
    public void onSessionAvailable(final Session session) {
        lock.lock();
        try {
            this.session = session;
        }
        finally {
            lock.unlock();
        }
    }

    @Override
    public void onAdsAvailable(final Session session) {
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
