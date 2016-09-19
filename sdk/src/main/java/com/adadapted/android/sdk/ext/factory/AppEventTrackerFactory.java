package com.adadapted.android.sdk.ext.factory;

import android.util.Log;

import com.adadapted.android.sdk.config.Config;
import com.adadapted.android.sdk.core.event.AppEventTracker;
import com.adadapted.android.sdk.core.session.SessionListener;
import com.adadapted.android.sdk.core.session.model.Session;
import com.adadapted.android.sdk.ext.http.HttpAppEventAdapter;
import com.adadapted.android.sdk.ext.json.JsonAppEventBuilder;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by chrisweeden on 9/16/16.
 */
public class AppEventTrackerFactory implements SessionListener {
    private static final String LOGTAG = AppEventTrackerFactory.class.getName();

    private static AppEventTrackerFactory sInstance;

    private static final Set<AppEventHolder> tempEvents = new HashSet<>();

    private static synchronized AppEventTracker getEventTracker() {
        if(sInstance == null) {
            sInstance = new AppEventTrackerFactory();
        }

        return sInstance.mEventTracker;
    }

    public static synchronized  void registerEvent(final String trackingId,
                                                   final String eventName,
                                                   final Map<String, String> eventParams) {
        if(getEventTracker() == null) {
            tempEvents.add(new AppEventHolder(trackingId, eventName, eventParams));
        }
        else {
            getEventTracker().trackAppEvent(trackingId, eventName, eventParams);
        }
    }

    public static synchronized  void publishEvents() {
        if(getEventTracker() != null && sInstance.mSession != null) {
            sInstance.mEventTracker.publishEvents(sInstance.mSession);
        }
    }

    private AppEventTracker mEventTracker;
    private Session mSession;

    private AppEventTrackerFactory() {
        SessionManagerFactory.addListener(this);
    }

    private String determineEndpoint(final Session session) {
        if(session.getDeviceInfo().isProd()) {
            return Config.Prod.URL_APP_EVENT_TRACK;
        }

        return Config.Sand.URL_APP_EVENT_TRACK;
    }

    @Override
    public void onSessionInitialized(final Session session) {
        mSession = session;
        final String endpoint = determineEndpoint(session);

        mEventTracker = new AppEventTracker(
                new HttpAppEventAdapter(endpoint),
                new JsonAppEventBuilder());

        for(AppEventHolder e : tempEvents) {
            mEventTracker.trackAppEvent(
                    e.getTrackingId(),
                    e.getEventName(),
                    e.getEventParams());
        }

        tempEvents.clear();
    }

    @Override
    public void onSessionInitFailed() {}

    @Override
    public void onNewAdsAvailable(Session session) {
        mSession = session;
    }

    private static class AppEventHolder {
        final String trackingId;
        final String eventName;
        final Map<String, String> eventParams;

        public AppEventHolder(final String trackingId,
                              final String eventName,
                              final Map<String, String> eventParams) {
            this.trackingId = trackingId;
            this.eventName = eventName;
            this.eventParams = eventParams;
        }

        public String getTrackingId() {
            return trackingId;
        }

        public String getEventName() {
            return eventName;
        }

        public Map<String, String> getEventParams() {
            return eventParams;
        }
    }
}
