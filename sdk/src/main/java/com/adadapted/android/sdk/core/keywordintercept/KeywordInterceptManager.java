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
public class KeywordInterceptManager implements KeywordInterceptAdapter.Listener {
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

    public KeywordInterceptManager(KeywordInterceptAdapter adapter,
                                   KeywordInterceptBuilder builder,
                                   KeywordInterceptRequestBuilder requestBuilder) {
        this.adapter = adapter;
        this.adapter.addListener(this);

        this.builder = builder;
        this.requestBuilder = requestBuilder;

        this.keywordInterceptEvents = new HashSet<>();
        this.failedRetries = 0;
    }

    public void init(Session session, DeviceInfo deviceInfo) {
        this.deviceInfo = deviceInfo;

        JSONObject request = requestBuilder.buildInitRequest(session, deviceInfo);
        adapter.init(request);
    }

    public void trackPresented(Session session, String term, String userInput) {
        KeywordInterceptEvent event = new KeywordInterceptEvent(
                session.getSessionId(),
                deviceInfo.getAppId(),
                deviceInfo.getUdid(),
                KeywordInterceptEvent.PRESENTED,
                userInput,
                term,
                deviceInfo.getSdkVersion());
        fileEvent(event);
    }

    public void trackSelected(Session session, String term, String userInput) {
        KeywordInterceptEvent event = new KeywordInterceptEvent(
                session.getSessionId(),
                deviceInfo.getAppId(),
                deviceInfo.getUdid(),
                KeywordInterceptEvent.SELECTED,
                userInput,
                term,
                deviceInfo.getSdkVersion());
        fileEvent(event);
    }

    public void publishEvents() {
        if(!isInitialized() || keywordInterceptEvents.isEmpty()) {
            Log.d(TAG, "No items queued to publish.");
        }
        else {
            Set<KeywordInterceptEvent> events = new HashSet<>(keywordInterceptEvents);
            keywordInterceptEvents.clear();

            JSONArray json = requestBuilder.buildTrackRequest(events);
            adapter.track(json);
        }
    }

    private void sendBatchRetry(JSONArray json) {
        if(failedRetries <= MAX_FAILED_RETRIES) {
            adapter.track(json);
        }
        else {
            Log.d(TAG, "Maximum failed retries. No longer sending batch retries.");
        }
    }

    private void fileEvent(KeywordInterceptEvent event) {
        Set<KeywordInterceptEvent> events = new HashSet<>(keywordInterceptEvents);

        for(KeywordInterceptEvent e : keywordInterceptEvents) {
            if(event.supercedes(e)) {
                Log.d(TAG, "Superceded: " + e);
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

    @Override
    public void onInitSuccess(JSONObject json) {
        keywordIntercept = builder.build(json);
        initialized = true;

        notifyInitSuccess();
    }

    @Override
    public void onInitFailed() {}

    @Override
    public void onTrackSuccess() {}

    @Override
    public void onTrackFailed(JSONArray json) {
        sendBatchRetry(json);
    }
}
