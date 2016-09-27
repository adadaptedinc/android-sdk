package com.adadapted.sdk.addit.ext.factory;

import com.adadapted.sdk.addit.config.Config;
import com.adadapted.sdk.addit.core.app.AppEventSink;
import com.adadapted.sdk.addit.core.app.AppEventTracker;
import com.adadapted.sdk.addit.core.app.RegisterAppEventCommand;
import com.adadapted.sdk.addit.core.app.RegisterAppEventInteractor;
import com.adadapted.sdk.addit.core.device.DeviceInfo;
import com.adadapted.sdk.addit.ext.concurrency.ThreadPoolInteractorExecuter;
import com.adadapted.sdk.addit.ext.http.HttpAppEventSink;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by chrisweeden on 9/26/16.
 */

public class AppEventTrackingManager implements DeviceInfoManager.Callback {
    private static AppEventTrackingManager sInstance;

    private static AppEventTrackingManager getInstance() {
        if(sInstance == null) {
            sInstance = new AppEventTrackingManager();
        }

        return sInstance;
    }

    public static void registerEvent(final String eventSource,
                                     final String eventName,
                                     final Map<String, String> eventParams) {
        if(getInstance().tracker == null) {
            getInstance().tempEventItems.add(new TempEventItem(eventSource, eventName, eventParams));
        }
        else {
            final TempEventItem item = new TempEventItem(eventSource, eventName, eventParams);
            getInstance().trackEvent(item);
        }
    }

    private final Set<TempEventItem> tempEventItems = new HashSet<>();

    private AppEventTracker tracker;

    private AppEventTrackingManager() {
        DeviceInfoManager.getInstance().getDeviceInfo(this);
    }

    private void trackEvent(final TempEventItem item) {
        final RegisterAppEventCommand command = new RegisterAppEventCommand(
                item.getEventSource(),
                item.getEventName(),
                item.getEventParams());
        final RegisterAppEventInteractor interactor = new RegisterAppEventInteractor(
                command,
                tracker);

        ThreadPoolInteractorExecuter.getInstance().executeInBackground(interactor);
    }

    @Override
    public void onDeviceInfoCollected(final DeviceInfo deviceInfo) {
        final String endpoint = determineEndpoint(deviceInfo);
        final AppEventSink sink = new HttpAppEventSink(endpoint);

        tracker = new AppEventTracker(deviceInfo, sink);

        Set<TempEventItem> eventItems = new HashSet<>(tempEventItems);
        tempEventItems.clear();
        for(TempEventItem i : eventItems) {
            trackEvent(i);
        }
    }

    private String determineEndpoint(final DeviceInfo deviceInfo) {
        if(deviceInfo.isProd()) {
            return Config.Prod.URL_APP_EVENT_TRACK;
        }

        return Config.Dev.URL_APP_EVENT_TRACK;
    }

    @Override
    public void onDeviceInfoCollectionError(Throwable e) {

    }

    private static class TempEventItem {
        private final String eventSource;
        private final String eventName;
        private final Map<String, String> eventParams;

        TempEventItem(final String eventSource,
                             final String eventName,
                             final Map<String, String> eventParams) {
            this.eventSource = eventSource == null ? "unknown" : eventSource;
            this.eventName = eventName == null ? "unknown" : eventName;
            this.eventParams = eventParams == null ? new HashMap<String, String>() : eventParams;
        }

        String getEventSource() {
            return eventSource;
        }

        String getEventName() {
            return eventName;
        }

        Map<String, String> getEventParams() {
            return eventParams;
        }
    }
}
