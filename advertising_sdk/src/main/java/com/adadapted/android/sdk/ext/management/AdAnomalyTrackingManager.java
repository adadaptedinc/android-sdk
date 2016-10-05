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
            tempAnomalies.add(anomalyHolder);
        }
        else {
            sInstance.trackAnomaly(anomalyHolder);
        }
    }

    private AdAnomalyTracker tracker;
    private Session session;

    private static final Set<AnomalyHolder> tempAnomalies = new HashSet<>();

    private AdAnomalyTrackingManager() {
        SessionManager.getSession(this);
    }

    @Override
    public void onSessionAvailable(final Session session) {
        this.session = session;
        this.tracker = new AdAnomalyTracker(
                new HttpAnomalyAdapter(determineEndpoint(session)),
                new JsonAnomalyBuilder());

        clearTempAnomalies();
    }

    @Override
    public void onNewAdsAvailable(Session session) {}

    private void clearTempAnomalies() {
        if(tempAnomalies.size() == 0) {
            return;
        }

        Set<AnomalyHolder> currentAnomalies = new HashSet<>(tempAnomalies);
        tempAnomalies.clear();

        for(final AnomalyHolder a : currentAnomalies) {
            trackAnomaly(a);
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
