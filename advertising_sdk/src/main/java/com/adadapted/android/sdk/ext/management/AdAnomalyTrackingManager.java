package com.adadapted.android.sdk.ext.management;

import com.adadapted.android.sdk.config.Config;
import com.adadapted.android.sdk.core.anomaly.AdAnomalyTracker;
import com.adadapted.android.sdk.core.anomaly.RegisterAdAnomalyCommand;
import com.adadapted.android.sdk.core.anomaly.RegisterAdAnomalyInteractor;
import com.adadapted.android.sdk.core.common.Interactor;
import com.adadapted.android.sdk.core.session.model.Session;
import com.adadapted.android.sdk.ext.concurrency.ThreadPoolInteractorExecuter;
import com.adadapted.android.sdk.ext.http.HttpAnomalyAdapter;
import com.adadapted.android.sdk.ext.json.JsonAnomalyBuilder;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by chrisweeden on 9/23/16.
 */

public class AdAnomalyTrackingManager implements SessionManager.Callback {
    private static AdAnomalyTrackingManager sInstance;

    private static synchronized AdAnomalyTrackingManager getsInstance() {
        if(sInstance == null) {
            sInstance = new AdAnomalyTrackingManager();
        }

        return sInstance;
    }

    public static synchronized void registerAnomaly(final String adId,
                                                    final String eventPath,
                                                    final String code,
                                                    final String message) {
        final AnomalyHolder anomalyHolder = new AnomalyHolder(adId, eventPath, code, message);
        if(getsInstance().tracker == null) {
            getsInstance().addTempAnomaly(anomalyHolder);
        }
        else {
            sInstance.trackAnomaly(anomalyHolder);
        }
    }

    private AdAnomalyTracker tracker;
    private Session session;

    private final Set<AnomalyHolder> tempAnomalies = new HashSet<>();
    private final Lock lock = new ReentrantLock();

    private AdAnomalyTrackingManager() {
        SessionManager.getSession(this);
    }

    private void addTempAnomaly(final AnomalyHolder anomalyHolder) {
        lock.lock();
        try {
            tempAnomalies.add(anomalyHolder);
        } finally {
            lock.unlock();
        }
    }

    private void trackTempAnomalies() {
        lock.lock();
        try {
            final Set<AnomalyHolder> currentAnomalies = new HashSet<>(tempAnomalies);
            tempAnomalies.clear();

            for(final AnomalyHolder a : currentAnomalies) {
                trackAnomaly(a);
            }
        } finally {
            lock.unlock();
        }
    }

    private void trackAnomaly(final AnomalyHolder anomalyHolder) {
        if(session == null || tracker == null) {
            return;
        }

        final RegisterAdAnomalyCommand command = new RegisterAdAnomalyCommand(
                session,
                anomalyHolder.getAdId(),
                anomalyHolder.getEventPath(),
                anomalyHolder.getCode(),
                anomalyHolder.getMessage());
        final Interactor interactor = new RegisterAdAnomalyInteractor(command, tracker);

        ThreadPoolInteractorExecuter.getInstance().executeInBackground(interactor);
    }

    @Override
    public void onSessionAvailable(final Session session) {
        SessionManager.removeCallback(this);

        this.session = session;
        this.tracker = new AdAnomalyTracker(
                new HttpAnomalyAdapter(determineEndpoint(session)),
                new JsonAnomalyBuilder());

        trackTempAnomalies();
    }

    @Override
    public void onNewAdsAvailable(Session session) {}

    private String determineEndpoint(final Session session) {
        if(session.getDeviceInfo().isProd()) {
            return Config.Prod.URL_ANOMALY_BATCH;
        }

        return Config.Sand.URL_ANOMALY_BATCH;
    }

    private static class AnomalyHolder {
        final String adId;
        final String eventPath;
        final String code;
        final String message;

        AnomalyHolder(final String adId,
                      final String eventPath,
                      final String code,
                      final String message) {
            this.adId = adId == null ? "unknown" : adId;
            this.eventPath = eventPath == null ? "unknown" : eventPath;
            this.code = code == null ? "unknown" : code;
            this.message = message == null ? "unknown" : message;
        }

        String getAdId() {
            return adId;
        }

        String getEventPath() {
            return eventPath;
        }

        String getCode() {
            return code;
        }

        String getMessage() {
            return message;
        }
    }
}
