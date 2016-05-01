package com.adadapted.android.sdk.ext.factory;

import com.adadapted.android.sdk.config.Config;
import com.adadapted.android.sdk.core.device.model.DeviceInfo;
import com.adadapted.android.sdk.core.event.EventTracker;
import com.adadapted.android.sdk.ext.http.HttpEventAdapter;
import com.adadapted.android.sdk.ext.json.JsonEventRequestBuilder;

/**
 * Created by chrisweeden on 5/26/15.
 */
public class EventTrackerFactory {
    private static final String LOGTAG = EventTrackerFactory.class.getName();

    private static EventTrackerFactory sInstance;

    private final EventTracker mEventTracker;

    private EventTrackerFactory(DeviceInfo deviceInfo) {
        mEventTracker = new EventTracker(new HttpEventAdapter(determineEndpoint(deviceInfo)),
                new JsonEventRequestBuilder());
    }

    public static synchronized EventTracker createEventTracker(DeviceInfo deviceInfo) {
        if(sInstance == null) {
            sInstance = new EventTrackerFactory(deviceInfo);
        }

        return sInstance.mEventTracker;
    }

    public static synchronized EventTracker getEventTracker() {
        return sInstance.mEventTracker;
    }

    private String determineEndpoint(DeviceInfo deviceInfo) {
        if(deviceInfo.isProd()) {
            return Config.Prod.URL_EVENT_BATCH;
        }

        return Config.Sand.URL_EVENT_BATCH;
    }
}
