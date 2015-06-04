package com.adadapted.android.sdk.core.session;

import com.adadapted.android.sdk.core.device.model.DeviceInfo;
import com.adadapted.android.sdk.core.session.model.Session;

import org.json.JSONObject;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by chrisweeden on 3/23/15.
 */
public class SessionManager implements SessionAdapter.Listener {
    private static final String TAG = SessionManager.class.getName();

    public interface Listener {
        void onSessionInitialized(Session session);
    }

    private final Set<Listener> listeners;

    private final SessionAdapter httpSessionAdapter;
    private final SessionRequestBuilder requestBuilder;
    private final SessionBuilder sessionBuilder;

    public SessionManager(SessionAdapter httpSessionAdapter,
                          SessionRequestBuilder requestBuilder,
                          SessionBuilder sessionBuilder) {
        this.listeners = new HashSet<>();

        this.httpSessionAdapter = httpSessionAdapter;
        this.httpSessionAdapter.addListener(this);

        this.requestBuilder = requestBuilder;

        this.sessionBuilder = sessionBuilder;
    }

    public void initialize(DeviceInfo deviceInfo) {
        JSONObject request = requestBuilder.buildSessionInitRequest(deviceInfo);
        httpSessionAdapter.sendInit(request);
    }

    public void reinitialize(DeviceInfo deviceInfo, Session session) {
        JSONObject request = requestBuilder.buildSessionReinitRequest(deviceInfo, session);
        httpSessionAdapter.sendReinit(request);
    }

    public void addListener(Listener listener) {
        listeners.add(listener);
    }

    public void removeListener(Listener listener) {
        listeners.remove(listener);
    }

    private void notifySessionInitialized(Session session) {
        for(Listener listener: listeners) {
           listener.onSessionInitialized(session);
        }
    }

    @Override
    public void onSessionRequestCompleted(JSONObject response) {
        Session session = sessionBuilder.buildSession(response);
        notifySessionInitialized(session);
    }

    @Override
    public String toString() {
        return "SessionManager{" +
                "listeners=" + listeners +
                ", httpSessionAdapter=" + httpSessionAdapter +
                '}';
    }
}
