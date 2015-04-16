package com.adadapted.android.sdk;

import android.content.Context;
import android.util.Log;

import com.adadapted.android.sdk.core.ad.AdFetcher;
import com.adadapted.android.sdk.core.device.BuildDeviceInfoParam;
import com.adadapted.android.sdk.core.device.DeviceInfo;
import com.adadapted.android.sdk.core.device.DeviceInfoBuilder;
import com.adadapted.android.sdk.core.event.EventTracker;
import com.adadapted.android.sdk.core.session.Session;
import com.adadapted.android.sdk.core.session.SessionManager;
import com.adadapted.android.sdk.ext.cache.ImageCache;
import com.adadapted.android.sdk.ext.http.HttpAdAdapter;
import com.adadapted.android.sdk.ext.http.HttpEventAdapter;
import com.adadapted.android.sdk.ext.http.HttpSessionAdapter;
import com.adadapted.android.sdk.ext.scheduler.AdRefreshScheduler;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by chrisweeden on 3/16/15.
 */
public class AdAdapted implements DeviceInfoBuilder.Listener,
        SessionManager.Listener, AdRefreshScheduler.Listener {
    private static final String TAG = "AdAdapted";

    private static AdAdapted instance;

    public interface Listener {
        void onSessionLoaded(Session session);
    }

    private final Set<Listener> listeners;

    private final  Context context;

    private DeviceInfo deviceInfo;

    private Session session;
    private boolean sessionLoaded;

    private AdFetcher adFetcher;
    private EventTracker eventTracker;
    private SessionManager sessionManager;

    private AdRefreshScheduler adRefreshScheduler;

    private final String adGetUrl;
    private final String eventBatchUrl;
    private final String sessionInitUrl;
    private final String sessionReinitUrl;

    private final String appId;
    private final String[] zones;
    private final String sdkVersion;

    private AdAdapted(Context context, String appId, String[] zones, boolean prodMode) {
        this.listeners = new HashSet<>();

        this.context = context;

        this.session = null;
        this.sessionLoaded = false;

        if(prodMode) {
            this.adGetUrl = context.getString(R.string.prod_ad_get_object_url);
            this.eventBatchUrl = context.getString(R.string.prod_event_batch_object_url);
            this.sessionInitUrl = context.getString(R.string.prod_session_init_object_url);
            this.sessionReinitUrl = context.getString(R.string.prod_session_reinit_object_url);
        }
        else {
            this.adGetUrl = context.getString(R.string.sandbox_ad_get_object_url);
            this.eventBatchUrl = context.getString(R.string.sandbox_event_batch_object_url);
            this.sessionInitUrl = context.getString(R.string.sandbox_session_init_object_url);
            this.sessionReinitUrl = context.getString(R.string.sandbox_session_reinit_object_url);
        }

        this.appId = appId;
        this.zones = zones;
        this.sdkVersion = context.getString(R.string.sdk_version);

        adRefreshScheduler = new AdRefreshScheduler();
        adRefreshScheduler.addListener(this);

        ImageCache.getInstance().purgeCache();
    }

    public static synchronized AdAdapted getInstance() {
        return instance;
    }

    public static synchronized void init(Context context, String appId, String[] zones, boolean prodMode) {
        Log.d(TAG, "init() called for appId: " + appId + " and zones: " + Arrays.toString(zones));
        instance = new AdAdapted(context, appId, zones, prodMode);

        getInstance().initialize();
    }

    private void initialize() {
        DeviceInfoBuilder builder = new DeviceInfoBuilder();
        builder.addListener(this);
        builder.execute(new BuildDeviceInfoParam(context, appId, zones, sdkVersion));
    }

    public Context getContext() {
        return context;
    }

    public DeviceInfo getDeviceInfo() {
        return deviceInfo;
    }

    public Session getSession() {
        return session;
    }

    private boolean sessionIsLoaded() {
        return sessionLoaded;
    }

    private AdFetcher getAdFetcher() {
        if(adFetcher == null) {
            adFetcher = new AdFetcher(new HttpAdAdapter(adGetUrl));
        }

        return adFetcher;
    }

    public EventTracker getEventTracker() {
        if(eventTracker == null) {
            this.eventTracker = new EventTracker(new HttpEventAdapter(eventBatchUrl));
        }

        return eventTracker;
    }

    private SessionManager getSessionManager() {
        if(sessionManager == null) {
            this.sessionManager = new SessionManager(new HttpSessionAdapter(sessionInitUrl, sessionReinitUrl));
            this.sessionManager.addListener(this);
        }

        return sessionManager;
    }

    public void addListener(AdAdapted.Listener listener) {
        listeners.add(listener);

        if(sessionIsLoaded()) {
            notifySessionLoaded();
        }
    }

    public void removeListener(AdAdapted.Listener listener) {
        listeners.remove(listener);
    }

    private void notifySessionLoaded() {
        for(Listener listener : listeners) {
            listener.onSessionLoaded(session);
        }
    }

    @Override
    public void onDeviceInfoCollected(DeviceInfo deviceInfo) {
        this.deviceInfo = deviceInfo;

        getSessionManager().initialize(deviceInfo);
    }

    public void onSessionInitialized(Session session) {
        this.session = session;
        this.sessionLoaded = true;

        adRefreshScheduler.schedule(session.getPollingInterval());

        notifySessionLoaded();
    }

    @Override
    public void onAdRefreshTimer() {
        Log.d(TAG, "AdRefreshScheduler fired.");

        getEventTracker().publishEvents();

        if(session.hasExpired()) {
            Log.d(TAG, "Session has expired.");
            getSessionManager().reinitialize(deviceInfo);
        }
        else {
            Log.d(TAG, "Session has NOT expired.");
            getAdFetcher().fetchAdsFor(deviceInfo, session);
        }
    }

    @Override
    public String toString() {
        return "AdAdapted{" +
                "listeners=" + listeners +
                ", context=" + context +
                ", deviceInfo=" + deviceInfo +
                ", session=" + session +
                ", sessionManager=" + sessionManager +
                ", eventTracker=" + eventTracker +
                ", sessionInitUrl='" + sessionInitUrl + '\'' +
                ", sessionReinitUrl='" + sessionReinitUrl + '\'' +
                ", eventBatchUrl='" + eventBatchUrl + '\'' +
                ", appId='" + appId + '\'' +
                ", zones=" + Arrays.toString(zones) +
                '}';
    }
}
