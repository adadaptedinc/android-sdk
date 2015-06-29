package com.adadapted.android.sdk.core.keywordintercept;

import com.adadapted.android.sdk.core.device.model.DeviceInfo;
import com.adadapted.android.sdk.core.keywordintercept.model.KeywordIntercept;
import com.adadapted.android.sdk.core.session.model.Session;

import org.json.JSONObject;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by chrisweeden on 6/23/15.
 */
public class KeywordInterceptManager implements KeywordInterceptAdapter.Listener<JSONObject> {
    public interface Listener {
        void onInitSuccess(KeywordIntercept keywordIntercept);
    }

    private final Set<Listener> listeners;

    private final KeywordInterceptAdapter adapter;
    private final KeywordInterceptBuilder builder;
    private final KeywordInterceptRequestBuilder requestBuilder;

    private KeywordIntercept keywordIntercept;

    private DeviceInfo deviceInfo;
    private boolean initialized = false;

    public KeywordInterceptManager(KeywordInterceptAdapter adapter,
                                   KeywordInterceptBuilder builder,
                                   KeywordInterceptRequestBuilder requestBuilder) {
        this.listeners = new HashSet<>();

        this.adapter = adapter;
        this.adapter.addListener(this);

        this.builder = builder;
        this.requestBuilder = requestBuilder;
    }

    public void init(Session session, DeviceInfo deviceInfo) {
        this.deviceInfo = deviceInfo;

        JSONObject request = (JSONObject)requestBuilder.buildInitRequest(session, deviceInfo);
        adapter.init(request);
    }

    public void trackPresented(Session session, String term, String userInput) {
        JSONObject request = (JSONObject)requestBuilder.buildTrackRequest(
                deviceInfo, session.getSessionId(), term, userInput, "presented");
        adapter.track(request);
    }

    public void trackSelected(Session session, String term, String userInput) {
        JSONObject request = (JSONObject)requestBuilder.buildTrackRequest(
                deviceInfo, session.getSessionId(), term, userInput, "selected");
        adapter.track(request);
    }

    private boolean isInitialized() {
        return initialized;
    }

    public void addListener(Listener listener) {
        listeners.add(listener);

        if(isInitialized()) {
            listener.onInitSuccess(keywordIntercept);
        }
    }

    public void removeListener(Listener listener) {
        listeners.remove(listener);
    }

    private void notifyInitSuccess() {
        for(Listener listener : listeners) {
            listener.onInitSuccess(keywordIntercept);
        }
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
    public void onTrackFailed() {}
}
