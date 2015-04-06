package com.adadapted.android.sdk;

import org.json.JSONObject;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by chrisweeden on 3/23/15.
 */
class SessionManager implements HttpSessionAdapter.Listener {
    private static final String TAG = SessionManager.class.getName();

    interface Listener {
        public void onSessionInitialized(Session session);
    }

    private final Set<Listener> listeners;

    private final SessionAdapter httpSessionAdapter;

    SessionManager(SessionAdapter httpSessionAdapter) {
        this.listeners = new HashSet<>();

        this.httpSessionAdapter = httpSessionAdapter;
        this.httpSessionAdapter.addListener(this);
    }

    void initialize(DeviceInfo deviceInfo) {
        JSONObject request = new SessionRequestBuilder().buildSessionRequestJson(deviceInfo);
        httpSessionAdapter.sendInit(request);
    }

    void addListener(Listener listener) {
        listeners.add(listener);
    }

    void removeListener(Listener listener) {
        listeners.remove(listener);
    }

    private void notifySessionInitialized(Session session) {
        for(Listener listener: listeners) {
           listener.onSessionInitialized(session);
        }
    }

    @Override
    public void onSessionRequestCompleted(JSONObject response) {
        SessionBuilder builder = new SessionBuilder();
        Session session = builder.buildSession(response);

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
