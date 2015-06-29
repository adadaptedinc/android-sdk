package com.adadapted.android.sdk.ext.factory;

import android.content.Context;

import com.adadapted.android.sdk.AdAdapted;
import com.adadapted.android.sdk.R;
import com.adadapted.android.sdk.core.event.EventTracker;
import com.adadapted.android.sdk.ext.http.HttpEventAdapter;
import com.adadapted.android.sdk.ext.json.JsonEventRequestBuilder;

/**
 * Created by chrisweeden on 5/26/15.
 */
public class EventTrackerFactory {
    private static EventTrackerFactory instance;

    public static synchronized EventTrackerFactory getInstance(Context context) {
        if(instance == null) {
            instance = new EventTrackerFactory(context);
        }

        return instance;
    }

    private final Context context;
    private EventTracker eventTracker;

    private EventTrackerFactory(Context context) {
        this.context = context;
    }

    public EventTracker createEventTracker() {
        if(eventTracker == null) {
            eventTracker = new EventTracker(new HttpEventAdapter(determineEndpoint()),
                    new JsonEventRequestBuilder());
        }

        return eventTracker;
    }

    private String determineEndpoint() {
        if(AdAdapted.getInstance().isProd()) {
            return context.getString(R.string.prod_event_batch_object_url);
        }

        return context.getString(R.string.sandbox_event_batch_object_url);
    }
}
