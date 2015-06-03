package com.adadapted.android.sdk;

import android.content.Context;
import android.util.Log;

import com.adadapted.android.sdk.core.ad.model.Ad;
import com.adadapted.android.sdk.core.ad.AdFetcher;
import com.adadapted.android.sdk.core.device.BuildDeviceInfoParam;
import com.adadapted.android.sdk.core.device.model.DeviceInfo;
import com.adadapted.android.sdk.core.device.DeviceInfoBuilder;
import com.adadapted.android.sdk.core.session.model.Session;
import com.adadapted.android.sdk.core.session.SessionManager;
import com.adadapted.android.sdk.core.zone.model.Zone;
import com.adadapted.android.sdk.ext.cache.ImageCache;
import com.adadapted.android.sdk.ext.factory.AdFetcherFactory;
import com.adadapted.android.sdk.ext.factory.SessionManagerFactory;
import com.adadapted.android.sdk.ext.scheduler.AdRefreshScheduler;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by chrisweeden on 3/16/15.
 */
public class AdAdapted implements DeviceInfoBuilder.Listener,
        SessionManager.Listener, AdFetcher.Listener {
    private static final String TAG = AdAdapted.class.getName();

    private static AdAdapted instance;

    public interface Listener {
        void onSessionLoaded(Session session);
        void onSessionAdsReloaded(Session session);
    }

    private final Set<Listener> listeners;

    private final Context context;

    private DeviceInfo deviceInfo;

    private Session session = null;
    private boolean sessionLoaded = false;

    private final boolean isProdMode;
    private final String sdkVersion;

    private AdAdapted(Context context, String appId, String[] zones, boolean isProdMode) {
        this.listeners = new HashSet<>();

        this.context = context;

        this.isProdMode = isProdMode;
        this.sdkVersion = context.getString(R.string.sdk_version);

        ImageCache.getInstance().purgeCache();

        DeviceInfoBuilder builder = new DeviceInfoBuilder();
        builder.addListener(this);
        builder.execute(new BuildDeviceInfoParam(context, appId, zones, sdkVersion));
    }

    public static synchronized AdAdapted getInstance() {
        return instance;
    }

    public static synchronized void init(Context context, String appId, String[] zones, boolean prodMode) {
        Log.d(TAG, "init() called for appId: " + appId + " and zones: " + Arrays.toString(zones));
        instance = new AdAdapted(context, appId, zones, prodMode);
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

    private boolean isSessionIsLoaded() {
        return sessionLoaded;
    }

    public boolean isProd() {
        return isProdMode;
    }

    public Ad getNextAdForZone(String zoneId) {
        Zone zone = session.getZone(zoneId);
        return (zone != null) ? zone.getNextAd(session.getPollingInterval()) : null;
    }

    private SessionManager getSessionManager() {
        SessionManager sessionManager = SessionManagerFactory.getInstance(context).createSessionManager();
        sessionManager.addListener(this);

        return sessionManager;
    }

    private void scheduleAdRefreshTimer() {
        new AdRefreshScheduler(context).schedule(
                session.getPollingInterval(),
                getSession(),
                getDeviceInfo());
    }

    public void addListener(AdAdapted.Listener listener) {
        listeners.add(listener);

        if(isSessionIsLoaded()) {
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

    private void notifySessionAdsReloaded() {
        for(Listener listener : listeners) {
            listener.onSessionAdsReloaded(session);
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

        AdFetcherFactory.getInstance(context).createAdFetcher().addListener(instance);

        scheduleAdRefreshTimer();
        notifySessionLoaded();
    }

    @Override
    public void onAdsRefreshed(Map<String, Zone> zones) {
        this.session.updateZones(zones);

        scheduleAdRefreshTimer();
        notifySessionAdsReloaded();
    }

    @Override
    public void onAdsNotRefreshed() {
        scheduleAdRefreshTimer();
        notifySessionAdsReloaded();
    }

    @Override
    public String toString() {
        return "AdAdapted{}";
    }
}
