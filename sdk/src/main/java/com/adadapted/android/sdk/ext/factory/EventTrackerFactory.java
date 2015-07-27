package com.adadapted.android.sdk.ext.factory;

import android.content.Context;

import com.adadapted.android.sdk.AdAdapted;
import com.adadapted.android.sdk.config.Config;
import com.adadapted.android.sdk.core.event.EventTracker;
import com.adadapted.android.sdk.ext.http.HttpEventAdapter;
import com.adadapted.android.sdk.ext.json.JsonEventRequestBuilder;

/**
 * Created by chrisweeden on 5/26/15.
 */
public class EventTrackerFactory {
    private static EventTrackerFactory instance;

    public static synchronized EventTrackerFactory getInstance() {
        if(instance == null) {
            instance = new EventTrackerFactory();
        }

        return instance;
    }

    private EventTracker eventTracker;

    private EventTrackerFactory() {}

    public EventTracker createEventTracker(Context context) {
        if(eventTracker == null) {
            eventTracker = new EventTracker(new HttpEventAdapter(determineEndpoint()),
                    new JsonEventRequestBuilder());
        }

        return eventTracker;
    }

    private String determineEndpoint() {
        if(AdAdapted.getInstance().isProd()) {
            return Config.Prod.URL_EVENT_BATCH;
        }

        return Config.Sand.URL_EVENT_BATCH;
    }
}
