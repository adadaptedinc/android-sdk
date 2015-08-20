package com.adadapted.android.sdk.core.keywordintercept;

import android.util.Log;

import com.adadapted.android.sdk.core.device.model.DeviceInfo;
import com.adadapted.android.sdk.core.keywordintercept.model.KeywordIntercept;
import com.adadapted.android.sdk.core.keywordintercept.model.KeywordInterceptEvent;
import com.adadapted.android.sdk.core.session.model.Session;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by chrisweeden on 6/23/15.
 */
public class KeywordInterceptManager {
    private static final String TAG = KeywordInterceptManager.class.getName();

    private static final int MAX_QUEUE_SIZE = 10;
    private static final int MAX_FAILED_RETRIES = 2;

    public interface Listener {
        void onKeywordInterceptInitSuccess(KeywordIntercept keywordIntercept);
    }

    private Listener listener;

    private final KeywordInterceptAdapter adapter;
    private final KeywordInterceptBuilder builder;
    private final KeywordInterceptRequestBuilder requestBuilder;

    private KeywordIntercept keywordIntercept;
    private Set<KeywordInterceptEvent> keywordInterceptEvents;

    private DeviceInfo deviceInfo;
    private boolean initialized = false;
    private int failedRetries;

    KeywordInterceptTrackListener keywordInterceptTrackListener = new KeywordInterceptTrackListener() {
        @Override
        public void onSuccess() {
            failedRetries = 0;
        }

        @Override
        public void onFailure(JSONArray json) {
            failedRetries++;
            sendBatchRetry(json);
        }
    };

    public KeywordInterceptManager(KeywordInterceptAdapter adapter,
                                   KeywordInterceptBuilder builder,
                                   KeywordInterceptRequestBuilder requestBuilder) {
        this.adapter = adapter;

        this.builder = builder;
        this.requestBuilder = requestBuilder;

        this.keywordInterceptEvents = new HashSet<>();
        this.failedRetries = 0;
    }

    public void init(Session session) {
        this.deviceInfo = session.getDeviceInfo();

        JSONObject request = requestBuilder.buildInitRequest(session);
        adapter.init(request, new KeywordInterceptInitListener() {
            @Override
            public void onSuccess(JSONObject json) {
                keywordIntercept = builder.build(json);
                initialized = true;

                notifyInitSuccess();
            }

            @Override
            public void onFailure() {}
        });
    }

    public void trackMatched(Session session, String term, String userInput) {
        trackEvent(session, term, userInput, KeywordInterceptEvent.MATCHED);
    }

    public void trackPresented(Session session, String term, String userInput) {
        trackEvent(session, term, userInput, KeywordInterceptEvent.PRESENTED);
    }

    public void trackSelected(Session session, String term, String userInput) {
        trackEvent(session, term, userInput, KeywordInterceptEvent.SELECTED);
    }

    private void trackEvent(Session session, String term, String userInput, String eventType) {
        String appId = deviceInfo.getAppId();
        String sessionId = session != null ? session.getSessionId() : "";
        String udid = deviceInfo.getUdid();
        String searchId = keywordIntercept.getSearchId();
        String sdkVersion = deviceInfo.getSdkVersion();

        KeywordInterceptEvent event = new KeywordInterceptEvent(appId, sessionId, udid, searchId,
                eventType, userInput, term, sdkVersion);
        fileEvent(event);
    }

    public void publishEvents() {
        if(isInitialized() && !keywordInterceptEvents.isEmpty()) {
            Set<KeywordInterceptEvent> events = new HashSet<>(keywordInterceptEvents);
            keywordInterceptEvents.clear();

            JSONArray json = requestBuilder.buildTrackRequest(events);
            adapter.track(json, keywordInterceptTrackListener);
        }
    }

    private void sendBatchRetry(JSONArray json) {
        if(failedRetries <= MAX_FAILED_RETRIES) {
            adapter.track(json, keywordInterceptTrackListener);
        }
        else {
            Log.w(TAG, "Maximum failed retries. No longer sending batch retries.");
        }
    }

    private void fileEvent(KeywordInterceptEvent event) {
        Set<KeywordInterceptEvent> events = new HashSet<>(keywordInterceptEvents);

        for(KeywordInterceptEvent e : keywordInterceptEvents) {
            if(event.supercedes(e)) {
                events.remove(e);
            }
        }

        events.add(event);

        keywordInterceptEvents.clear();
        keywordInterceptEvents.addAll(events);

        if(keywordInterceptEvents.size() >= MAX_QUEUE_SIZE) {
            publishEvents();
        }
    }

    private boolean isInitialized() {
        return initialized;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public void removeListener() {
        this.listener = null;
    }

    private void notifyInitSuccess() {
       listener.onKeywordInterceptInitSuccess(keywordIntercept);
    }
}
