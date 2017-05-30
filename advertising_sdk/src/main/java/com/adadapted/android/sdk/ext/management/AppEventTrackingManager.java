package com.adadapted.android.sdk.ext.management;

import com.adadapted.android.sdk.config.Config;
import com.adadapted.android.sdk.core.device.DeviceInfo;
import com.adadapted.android.sdk.core.event.AppEventSink;
import com.adadapted.android.sdk.core.event.AppEventTracker;
import com.adadapted.android.sdk.core.event.RegisterAppEventCommand;
import com.adadapted.android.sdk.core.event.RegisterAppEventInteractor;
import com.adadapted.android.sdk.core.session.model.Session;
import com.adadapted.android.sdk.ext.concurrency.ThreadPoolInteractorExecuter;
import com.adadapted.android.sdk.ext.http.HttpAppEventSink;
import com.adadapted.android.sdk.ext.json.JsonAppEventBuilder;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by chrisweeden on 9/26/16.
 */
public class AppEventTrackingManager implements SessionManager.Callback {
    private static AppEventTrackingManager sInstance;

    private static AppEventTrackingManager getInstance() {
        if(sInstance == null) {
            sInstance = new AppEventTrackingManager();
        }

        return sInstance;
    }

    public static synchronized void registerEvent(final String eventSource,
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
        SessionManager.getSession(this);
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

    private String determineEndpoint(final DeviceInfo deviceInfo) {
        if(deviceInfo.isProd()) {
            return Config.Prod.URL_APP_EVENT_TRACK;
        }

        return Config.Sand.URL_APP_EVENT_TRACK;
    }

    @Override
    public void onSessionAvailable(final Session session) {
        if(tracker != null) {
            return;
        }

        final String endpoint = determineEndpoint(session.getDeviceInfo());
        final AppEventSink sink = new HttpAppEventSink(endpoint);

        tracker = new AppEventTracker(session, sink, new JsonAppEventBuilder());

        final Set<TempEventItem> eventItems = new HashSet<>(tempEventItems);
        tempEventItems.clear();
        for(final TempEventItem i : eventItems) {
            trackEvent(i);
        }

        SessionManager.removeCallback(this);
    }

    @Override
    public void onNewAdsAvailable(final Session session) {}

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
