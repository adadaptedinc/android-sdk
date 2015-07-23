package com.adadapted.android.sdk.core.session;

import android.content.Context;
import android.util.Log;

import com.adadapted.android.sdk.AdAdapted;
import com.adadapted.android.sdk.core.ad.AdFetcher;
import com.adadapted.android.sdk.core.device.model.DeviceInfo;
import com.adadapted.android.sdk.core.session.model.Session;
import com.adadapted.android.sdk.core.zone.model.Zone;
import com.adadapted.android.sdk.ext.scheduler.AdRefreshScheduler;
import com.adadapted.android.sdk.ext.scheduler.EventFlushScheduler;

import org.json.JSONObject;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by chrisweeden on 3/23/15.
 */
public class SessionManager implements SessionAdapter.Listener, AdFetcher.Listener {
    private static final String TAG = SessionManager.class.getName();

    public interface Listener {
        void onSessionInitialized(Session session);
        void onSessionInitFailed();
        void onSessionNotReinitialized();
        void onNewAdsAvailable(Session session);
    }

    private final Set<Listener> listeners;

    private final Context context;
    private final SessionAdapter httpSessionAdapter;
    private final SessionRequestBuilder requestBuilder;
    private final SessionBuilder sessionBuilder;

    private Session currentSession;
    private boolean sessionLoaded = false;

    public SessionManager(Context context,
                          SessionAdapter httpSessionAdapter,
                          SessionRequestBuilder requestBuilder,
                          SessionBuilder sessionBuilder) {
        this.context = context;
        this.listeners = new HashSet<>();

        this.httpSessionAdapter = httpSessionAdapter;
        this.httpSessionAdapter.addListener(this);

        this.requestBuilder = requestBuilder;

        this.sessionBuilder = sessionBuilder;
    }

    public Session getCurrentSession() {
        return currentSession;
    }

    public void initialize(DeviceInfo deviceInfo) {
        JSONObject request = requestBuilder.buildSessionInitRequest(deviceInfo);
        httpSessionAdapter.sendInit(request);
    }

    public void reinitialize(DeviceInfo deviceInfo, Session session) {
        JSONObject request = requestBuilder.buildSessionReinitRequest(deviceInfo, session);
        httpSessionAdapter.sendReinit(request);
    }

    public void scheduleAdRefresh() {
        new AdRefreshScheduler(context).schedule(getCurrentSession(),
                AdAdapted.getInstance().getDeviceInfo());
    }

    public void addListener(Listener listener) {
        if(sessionLoaded) {
            listener.onSessionInitialized(currentSession);
        }

        listeners.add(listener);
    }

    public void removeListener(Listener listener) {
        listeners.remove(listener);
    }

    private void notifySessionInitialized() {
        for(Listener listener: listeners) {
           listener.onSessionInitialized(currentSession);
        }
    }

    private void notifySessionInitFailed() {
        for(Listener listener: listeners) {
            listener.onSessionInitFailed();
        }
    }

    private void notifySessionNotReinitialized() {
        for(Listener listener: listeners) {
            listener.onSessionNotReinitialized();
        }
    }

    private void notifyNewAdsAvailable() {
        for(Listener listener: listeners) {
            listener.onNewAdsAvailable(currentSession);
        }
    }

    @Override
    public void onSessionInitRequestCompleted(JSONObject response) {
        Log.d(TAG, "onSessionInitRequestCompleted() Called");

        currentSession = sessionBuilder.buildSession(response);
        sessionLoaded = true;

        scheduleAdRefresh();
        new EventFlushScheduler(context).start(20000L);

        notifySessionInitialized();
    }

    @Override
    public void onSessionInitRequestFailed() {
        notifySessionInitFailed();
    }

    @Override
    public void onSessionReinitRequestNoContent() {
        notifySessionNotReinitialized();
    }

    @Override
    public void onSessionReinitRequestFailed() {
        notifySessionNotReinitialized();
    }

    @Override
    public void onAdsRefreshed(Map<String, Zone> zones) {
        currentSession.updateZones(zones);
        scheduleAdRefresh();
        notifyNewAdsAvailable();
    }

    @Override
    public void onAdsNotRefreshed() {}
}
