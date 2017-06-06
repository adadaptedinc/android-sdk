package com.adadapted.android.sdk.ext.management;

import com.adadapted.android.sdk.config.Config;
import com.adadapted.android.sdk.core.common.Interactor;
import com.adadapted.android.sdk.core.device.DeviceInfo;
import com.adadapted.android.sdk.core.keywordintercept.KeywordInterceptEventSink;
import com.adadapted.android.sdk.core.keywordintercept.KeywordInterceptEventTracker;
import com.adadapted.android.sdk.core.keywordintercept.PublishKeywordInterceptEventsInteractor;
import com.adadapted.android.sdk.core.keywordintercept.RegisterKeywordInterceptEventCommand;
import com.adadapted.android.sdk.core.keywordintercept.RegisterKeywordInterceptEventInteractor;
import com.adadapted.android.sdk.core.keywordintercept.model.KeywordInterceptEvent;
import com.adadapted.android.sdk.core.session.model.Session;
import com.adadapted.android.sdk.ext.concurrency.ThreadPoolInteractorExecuter;
import com.adadapted.android.sdk.ext.http.HttpKeywordInterceptEventSink;
import com.adadapted.android.sdk.ext.json.JsonKeywordInterceptEventBuilder;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by chrisweeden on 9/30/16.
 */
public class KeywordInterceptEventTrackingManager implements SessionManager.Callback {
    private static KeywordInterceptEventTrackingManager sInstance;

    private static synchronized KeywordInterceptEventTrackingManager getInstance() {
        if(sInstance == null) {
            sInstance = new KeywordInterceptEventTrackingManager();
        }

        return sInstance;
    }

    public static synchronized void trackMatched(final Session session,
                                                 final String term,
                                                 final String userInput) {
        registerEvent(session, term, userInput, KeywordInterceptEvent.MATCHED);
    }

    public static synchronized void trackPresented(final Session session,
                                                   final String term,
                                                   final String userInput) {
        registerEvent(session, term, userInput, KeywordInterceptEvent.PRESENTED);
    }

    public static synchronized void trackSelected(final Session session,
                                                  final String term,
                                                  final String userInput) {
        registerEvent(session, term, userInput, KeywordInterceptEvent.SELECTED);
    }

    private static synchronized void registerEvent(final Session session,
                                                   final String term,
                                                   final String userInput,
                                                   final String eventType) {
        if(session == null) {
            return;
        }

        final TempKIEvent tempEvent = new TempKIEvent(session, term, userInput, eventType);
        if(getInstance().tracker == null) {
            getInstance().addTempEvent(tempEvent);
        }
        else {
            getInstance().trackEvent(tempEvent);
        }
    }

    public static synchronized void publish() {
        if(getInstance().tracker != null) {
            getInstance().publishEvents();
        }
    }

    private KeywordInterceptEventTracker tracker;
    private static final Set<TempKIEvent> tempKIEvents = new HashSet<>();
    private final Lock lock = new ReentrantLock();

    private KeywordInterceptEventTrackingManager() {
        SessionManager.getSession(this);
    }

    private String determineTrackEndpoint(final DeviceInfo deviceInfo) {
        if(deviceInfo.isProd()) {
            return Config.Prod.URL_KI_TRACK;
        }

        return Config.Sand.URL_KI_TRACK;
    }

    private void publishEvents() {
        trackTempEvents();

        final Interactor interactor = new PublishKeywordInterceptEventsInteractor(tracker);
        ThreadPoolInteractorExecuter.getInstance().executeInBackground(interactor);
    }

    private void trackEvent(final TempKIEvent tempEvent) {
        final String searchId = KeywordInterceptManager.getKeywordIntercept().getSearchId();
        final RegisterKeywordInterceptEventCommand command = new RegisterKeywordInterceptEventCommand(
                tempEvent.getSession(),
                searchId,
                tempEvent.getTerm(),
                tempEvent.getUserInput(),
                tempEvent.getEventType()
        );
        final Interactor interactor = new RegisterKeywordInterceptEventInteractor(command, tracker);

        ThreadPoolInteractorExecuter.getInstance().executeInBackground(interactor);
    }

    private void addTempEvent(final TempKIEvent event) {
        lock.lock();
        try {
            tempKIEvents.add(event);
        } finally {
            lock.unlock();
        }
    }

    private void trackTempEvents() {
        lock.lock();
        try {
            final Set<TempKIEvent> currentEvents = new HashSet<>(tempKIEvents);
            tempKIEvents.clear();

            for(final TempKIEvent e : currentEvents) {
                trackEvent(e);
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void onSessionAvailable(final Session session) {
        SessionManager.removeCallback(this);

        final String endpoint = determineTrackEndpoint(session.getDeviceInfo());
        final KeywordInterceptEventSink sink = new HttpKeywordInterceptEventSink(endpoint);

        tracker = new KeywordInterceptEventTracker(sink, new JsonKeywordInterceptEventBuilder());
    }

    @Override
    public void onNewAdsAvailable(final Session session) {}

    private static final class TempKIEvent {
        private final Session session;
        private final String term;
        private final String userInput;
        private final String eventType;

        TempKIEvent(final Session session,
                           final String term,
                           final String userInput,
                           final String eventType) {
            this.session = session;
            this.term = term == null ? "unknown" : term;
            this.userInput = userInput == null ? "unknown" : userInput;
            this.eventType = eventType == null ? "unknown" : eventType;
        }

        Session getSession() {
            return session;
        }

        String getTerm() {
            return term;
        }

        String getUserInput() {
            return userInput;
        }

        String getEventType() {
            return eventType;
        }
    }
}
