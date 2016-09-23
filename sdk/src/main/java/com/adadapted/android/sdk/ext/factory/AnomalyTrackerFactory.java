package com.adadapted.android.sdk.ext.factory;

import com.adadapted.android.sdk.config.Config;
import com.adadapted.android.sdk.core.anomaly.AnomalyTracker;
import com.adadapted.android.sdk.core.session.SessionListener;
import com.adadapted.android.sdk.core.session.model.Session;
import com.adadapted.android.sdk.ext.http.HttpAnomalyAdapter;
import com.adadapted.android.sdk.ext.json.JsonAnomalyBuilder;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by chrisweeden on 9/23/16.
 */

public class AnomalyTrackerFactory implements SessionListener {
    private static AnomalyTrackerFactory sInstance;

    private static final Set<AnomalyTrackerFactory.AnomalyHolder> tempAnomalies = new HashSet<>();

    private static synchronized AnomalyTracker getAnomalyTracker() {
        if(sInstance == null) {
            sInstance = new AnomalyTrackerFactory();
        }

        return sInstance.mAnomalyTracker;
    }

    public static synchronized void registerAnomaly(final String adId,
                                                    final String eventPath,
                                                    final String code,
                                                    final String message) {
        if(getAnomalyTracker() == null) {
            tempAnomalies.add(new AnomalyHolder(adId, eventPath, code, message));
        }
        else {
            sInstance.mAnomalyTracker.registerAnomaly(sInstance.mSession, adId, eventPath, code, message);
        }
    }

    public static synchronized void publishEvents() {
        if(getAnomalyTracker() != null && sInstance.mSession != null) {
            sInstance.mAnomalyTracker.publishEvents();
        }
    }

    private AnomalyTracker mAnomalyTracker;
    private Session mSession;

    private AnomalyTrackerFactory() {
        SessionManagerFactory.addListener(this);
    }

    @Override
    public void onSessionInitialized(final Session session) {
        mSession = session;

        mAnomalyTracker = new AnomalyTracker(
                new HttpAnomalyAdapter(determineEndpoint(session)),
                new JsonAnomalyBuilder());

        for(final AnomalyHolder a : tempAnomalies) {
            mAnomalyTracker.registerAnomaly(
                    session,
                    a.getAdId(),
                    a.getEventPath(),
                    a.getCode(),
                    a.getMessage());
        }

        tempAnomalies.clear();
    }

    private String determineEndpoint(final Session session) {
        if(session.getDeviceInfo().isProd()) {
            return Config.Prod.URL_ANOMALY_BATCH;
        }

        return Config.Sand.URL_ANOMALY_BATCH;
    }

    @Override
    public void onSessionInitFailed() {}

    @Override
    public void onNewAdsAvailable(final Session session) {
        mSession = session;
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
            this.adId = adId;
            this.eventPath = eventPath;
            this.code = code;
            this.message = message;
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
