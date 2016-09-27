package com.adadapted.sdk.addit.ext.factory;

import com.adadapted.sdk.addit.core.anomaly.AnomalySink;
import com.adadapted.sdk.addit.core.anomaly.AnomalyTracker;
import com.adadapted.sdk.addit.core.anomaly.TrackAnomalyCommand;
import com.adadapted.sdk.addit.core.anomaly.TrackAnomalyInteractor;
import com.adadapted.sdk.addit.core.common.Interactor;
import com.adadapted.sdk.addit.core.config.Config;
import com.adadapted.sdk.addit.core.device.DeviceInfo;
import com.adadapted.sdk.addit.ext.concurrency.ThreadPoolInteractorExecuter;
import com.adadapted.sdk.addit.ext.http.HttpAnomalySink;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by chrisweeden on 9/26/16.
 */
public class AnomalyTrackingManager implements DeviceInfoManager.Callback {
    private static AnomalyTrackingManager sInstance;

    private static AnomalyTrackingManager getInstance() {
        if(sInstance == null) {
            sInstance = new AnomalyTrackingManager();
        }

        return sInstance;
    }

    public static void registerAnomaly(final String eventPath,
                                       final String code,
                                       final String message) {
        if(getInstance().tracker != null) {
            TempAnomaly anomaly = new TempAnomaly(eventPath, code, message);
            getInstance().trackAnomaly(anomaly);
        }
        else {
            getInstance().tempAnomalies.add(new TempAnomaly(eventPath, code, message));
        }
    }

    private final Set<TempAnomaly> tempAnomalies = new HashSet<>();

    private AnomalyTracker tracker;

    private AnomalyTrackingManager() {
        DeviceInfoManager.getInstance().getDeviceInfo(this);
    }

    private void trackAnomaly(final TempAnomaly anomaly) {
        final TrackAnomalyCommand command = new TrackAnomalyCommand(
                anomaly.getEventPath(),
                anomaly.getCode(),
                anomaly.getMessage());
        final Interactor interactor = new TrackAnomalyInteractor(command, tracker);

        ThreadPoolInteractorExecuter.getInstance().executeInBackground(interactor);
    }

    @Override
    public void onDeviceInfoCollected(final DeviceInfo deviceInfo) {
        final AnomalySink sink = new HttpAnomalySink(determineEndpoint(deviceInfo));
        tracker = new AnomalyTracker(deviceInfo, sink);

        Set<TempAnomaly> anomalies = new HashSet<>(tempAnomalies);
        tempAnomalies.clear();
        for(TempAnomaly a : anomalies) {
            trackAnomaly(a);
        }
    }

    private String determineEndpoint(final DeviceInfo deviceInfo) {
        if(deviceInfo.isProd()) {
            return Config.Prod.URL_ANOMALY_TRACK;
        }

        return Config.Dev.URL_ANOMALY_TRACK;
    }

    @Override
    public void onDeviceInfoCollectionError(Throwable e) {}

    private static class TempAnomaly {
        private final String eventPath;
        private final String code;
        private final String message;

        TempAnomaly(final String eventPath,
                           final String code,
                           final String message) {
            this.eventPath = eventPath == null ? "" : eventPath;
            this.code = code == null ? "" : code;
            this.message = message == null ? "" : message;
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
