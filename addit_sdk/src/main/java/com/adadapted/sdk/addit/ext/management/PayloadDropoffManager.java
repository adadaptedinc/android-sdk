package com.adadapted.sdk.addit.ext.management;

import com.adadapted.sdk.addit.config.Config;
import com.adadapted.sdk.addit.core.device.DeviceInfo;
import com.adadapted.sdk.addit.core.payload.PayloadEventTracker;
import com.adadapted.sdk.addit.core.payload.TrackPayloadDeliveryCommand;
import com.adadapted.sdk.addit.core.payload.TrackPayloadDeliveryInteractor;
import com.adadapted.sdk.addit.ext.concurrency.ThreadPoolInteractorExecuter;
import com.adadapted.sdk.addit.ext.http.HttpPayloadEventSink;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by chrisweeden on 2/10/17.
 */
public class PayloadDropoffManager implements DeviceInfoManager.Callback {
    private static PayloadDropoffManager sInstance;

    private PayloadEventTracker tracker;
    private final Set<TempEvent> tempEvents = new HashSet<>();

    private PayloadDropoffManager() {
        DeviceInfoManager.getInstance().getDeviceInfo(this);
    }

    private static PayloadDropoffManager getInstance() {
        if(sInstance == null) {
            sInstance = new PayloadDropoffManager();
        }

        return sInstance;
    }

    @Override
    public void onDeviceInfoCollected(final DeviceInfo deviceInfo) {
        if(deviceInfo == null) {
            return;
        }

        this.tracker = new PayloadEventTracker(deviceInfo, new HttpPayloadEventSink(determineEndpoint(deviceInfo)));

        final Set<TempEvent> events = new HashSet<>(tempEvents);
        tempEvents.clear();
        for(TempEvent e : events) {
            performTrackDropoff(e.payloadId, e.result);
        }
    }

    private String determineEndpoint(final DeviceInfo deviceInfo) {
        if(deviceInfo.isProd()) {
            return Config.Prod.URL_APP_PAYLOAD_TRACK;
        }

        return Config.Dev.URL_APP_PAYLOAD_TRACK;
    }

    private void performTrackDropoff(final String payloadId,
                                     final String result) {
        DeviceInfoManager.getInstance().getDeviceInfo(new DeviceInfoManager.Callback() {
            @Override
            public void onDeviceInfoCollected(final DeviceInfo deviceInfo) {
                final TrackPayloadDeliveryCommand command = new TrackPayloadDeliveryCommand(payloadId, result);
                final TrackPayloadDeliveryInteractor interactor = new TrackPayloadDeliveryInteractor(command, tracker);

                ThreadPoolInteractorExecuter.getInstance().executeInBackground(interactor);
            }
        });
    }

    public synchronized static void trackDelivered(final String payloadId) {
        if(payloadId == null) {
            return;
        }

        if(getInstance().tracker == null) {
            getInstance().tempEvents.add(new TempEvent(payloadId, "delivered"));
        } else {
            getInstance().performTrackDropoff(payloadId, "delivered");
        }
    }

    public synchronized static void trackRejected(final String payloadId) {
        if(payloadId == null) {
            return;
        }

        if(getInstance().tracker == null) {
            getInstance().tempEvents.add(new TempEvent(payloadId, "rejected"));
        } else {
            getInstance().performTrackDropoff(payloadId, "rejected");
        }
    }

    private static class TempEvent {
        final String payloadId;
        final String result;

        TempEvent(final String payloadId, final String result) {
            this.payloadId = payloadId;
            this.result = result;
        }

        public String getPayloadId() {
            return payloadId;
        }

        public String getResult() {
            return result;
        }
    }
}
