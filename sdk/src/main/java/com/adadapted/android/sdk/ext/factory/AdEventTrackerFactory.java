package com.adadapted.android.sdk.ext.factory;

import com.adadapted.android.sdk.config.Config;
import com.adadapted.android.sdk.core.device.model.DeviceInfo;
import com.adadapted.android.sdk.core.event.AdEventTracker;
import com.adadapted.android.sdk.ext.http.HttpAdEventAdapter;
import com.adadapted.android.sdk.ext.json.JsonAdEventRequestBuilder;

/**
 * Created by chrisweeden on 5/26/15.
 */
public class AdEventTrackerFactory {
    private static final String LOGTAG = AdEventTrackerFactory.class.getName();

    private static AdEventTrackerFactory sInstance;

    private final AdEventTracker mEventTracker;

    private AdEventTrackerFactory(final DeviceInfo deviceInfo) {
        final String endpoint = determineEndpoint(deviceInfo);

        mEventTracker = new AdEventTracker(
                new HttpAdEventAdapter(endpoint),
                new JsonAdEventRequestBuilder());
    }

    public static synchronized AdEventTracker createEventTracker(final DeviceInfo deviceInfo) {
        if(sInstance == null) {
            sInstance = new AdEventTrackerFactory(deviceInfo);
        }

        return sInstance.mEventTracker;
    }

    public static synchronized AdEventTracker getEventTracker() {
        return sInstance.mEventTracker;
    }

    private String determineEndpoint(final DeviceInfo deviceInfo) {
        if(deviceInfo.isProd()) {
            return Config.Prod.URL_EVENT_BATCH;
        }

        return Config.Sand.URL_EVENT_BATCH;
    }
}
