package com.adadapted.android.sdk.ext.management;

import com.adadapted.android.sdk.config.Config;
import com.adadapted.android.sdk.core.ad.AdEventTracker;
import com.adadapted.android.sdk.core.ad.PublishAdEventsInteractor;
import com.adadapted.android.sdk.core.ad.RegisterAdEventCommand;
import com.adadapted.android.sdk.core.ad.RegisterAdEventInteractor;
import com.adadapted.android.sdk.core.ad.model.Ad;
import com.adadapted.android.sdk.core.common.Interactor;
import com.adadapted.android.sdk.core.device.DeviceInfo;
import com.adadapted.android.sdk.core.event.model.AdEvent;
import com.adadapted.android.sdk.core.session.model.Session;
import com.adadapted.android.sdk.ext.concurrency.ThreadPoolInteractorExecuter;
import com.adadapted.android.sdk.ext.http.HttpAdEventSink;
import com.adadapted.android.sdk.ext.json.JsonAdEventRequestBuilder;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by chrisweeden on 5/26/15.
 */
public class AdEventTrackingManager implements DeviceInfoManager.Callback {
    private static final String LOGTAG = AdEventTrackingManager.class.getName();

    private static AdEventTrackingManager sInstance;

    private static synchronized AdEventTrackingManager getInstance() {
        if(sInstance == null) {
            sInstance = new AdEventTrackingManager();
        }

        return sInstance;
    }

    public static synchronized void trackImpressionBeginEvent(final Session session, final Ad ad) {
        registerAdEvent(session, ad, AdEvent.Types.IMPRESSION, "");
    }

    public static synchronized void trackImpressionEndEvent(final Session session, final Ad ad) {
        registerAdEvent(session, ad, AdEvent.Types.IMPRESSION_END, "");
    }

    public static synchronized void trackInteractionEvent(final Session session, final Ad ad) {
        registerAdEvent(session, ad, AdEvent.Types.INTERACTION, "");
    }

    public static synchronized void trackPopupBeginEvent(final Session session, final Ad ad) {
        registerAdEvent(session, ad, AdEvent.Types.POPUP_BEGIN, "");
    }

    public static synchronized void trackPopupEndEvent(final Session session, final Ad ad) {
        registerAdEvent(session, ad, AdEvent.Types.POPUP_END, "");
    }

    public static synchronized void trackCustomEvent(final Session session, final Ad ad, final String eventName) {
        registerAdEvent(session, ad, AdEvent.Types.CUSTOM, eventName);
    }

    private static synchronized void registerAdEvent(final Session session,
                                                     final Ad ad,
                                                     final String eventType,
                                                     final String eventName) {
        final TempAdEvent tempAdEvent = new TempAdEvent(session, ad, eventType, eventName);
        if(getInstance().tracker == null) {
            getInstance().tempAdEvents.add(tempAdEvent);
        }
        else {
            getInstance().trackAdEvent(tempAdEvent);
        }
    }

    public static synchronized void publish() {
        if(getInstance().tracker != null) {
            getInstance().publishAdEvents();
        }
    }

    private static final Set<Callback> callbacks = new HashSet<>();

    public static synchronized void addCallback(final Callback callback) {
        callbacks.add(callback);
    }

    public static synchronized void removeCallback(final Callback callback) {
        callbacks.remove(callback);
    }

    private AdEventTracker tracker;
    private final Set<TempAdEvent> tempAdEvents = new HashSet<>();

    private AdEventTrackingManager() {
        DeviceInfoManager.getInstance().getDeviceInfo(this);
    }

    private void trackAdEvent(final TempAdEvent event) {
        final RegisterAdEventCommand command = new RegisterAdEventCommand(
                event.getSession(),
                event.getAd(),
                event.getEventType(),
                event.getEventName()
        );
        final Interactor interactor = new RegisterAdEventInteractor(command, tracker);

        ThreadPoolInteractorExecuter.getInstance().executeInBackground(interactor);

        notifyOnAdEventTracked(event);
    }

    private void publishAdEvents() {
        clearTempEvents();

        final Interactor interactor = new PublishAdEventsInteractor(tracker);
        ThreadPoolInteractorExecuter.getInstance().executeInBackground(interactor);
    }

    private void clearTempEvents() {
        final Set<TempAdEvent> currentAdEvents = new HashSet<>(tempAdEvents);
        tempAdEvents.clear();

        for(final TempAdEvent e : currentAdEvents) {
            trackAdEvent(e);
        }
    }

    @Override
    public void onDeviceInfoCollected(final DeviceInfo deviceInfo) {
        final String endpoint = determineEndpoint(deviceInfo);

        tracker = new AdEventTracker(
                new HttpAdEventSink(endpoint),
                new JsonAdEventRequestBuilder());

        clearTempEvents();
    }

    private String determineEndpoint(final DeviceInfo deviceInfo) {
        if(deviceInfo.isProd()) {
            return Config.Prod.URL_EVENT_BATCH;
        }

        return Config.Sand.URL_EVENT_BATCH;
    }

    private void notifyOnAdEventTracked(TempAdEvent event) {
        final Set<Callback> currentCallbacks = new HashSet<>(callbacks);
        for(Callback c : currentCallbacks) {
            c.onAdEventTracked(new AdEvent(event.getEventType(), event.getAd().getZoneId()));
        }
    }

    public interface Callback {
        void onAdEventTracked(AdEvent event);
    }

    private static final class TempAdEvent {
        private final Session session;
        private final Ad ad;
        private final String eventType;
        private final String eventName;

        TempAdEvent(final Session session,
                    final Ad ad,
                    final String eventType,
                    final String eventName) {
            this.session = session;
            this.ad = ad;
            this.eventType = eventType;
            this.eventName = eventName;
        }

        Session getSession() {
            return session;
        }

        Ad getAd() {
            return ad;
        }

        String getEventType() {
            return eventType;
        }

        String getEventName() {
            return eventName;
        }
    }
}
