package com.adadapted.android.sdk;

import android.content.Context;
import android.util.Log;

import com.adadapted.android.sdk.core.content.ContentPayload;
import com.adadapted.android.sdk.core.device.BuildDeviceInfoParam;
import com.adadapted.android.sdk.core.device.model.DeviceInfo;
import com.adadapted.android.sdk.core.device.DeviceInfoBuilder;
import com.adadapted.android.sdk.core.session.SessionManager;
import com.adadapted.android.sdk.ext.cache.ImageCache;
import com.adadapted.android.sdk.ext.factory.SessionManagerFactory;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by chrisweeden on 3/16/15.
 */
public class AdAdapted implements DeviceInfoBuilder.Listener {
    private static final String TAG = AdAdapted.class.getName();

    private static AdAdapted instance;

    public interface Listener {
        void onAdImpression(String zoneId, String eventType);
        void onAdClick(String zoneId, String eventType);
        void onContentAvailable(ContentPayload contentPayload);
    }

    private final Set<Listener> listeners;

    private final Context context;

    private DeviceInfo deviceInfo;

    private final boolean isProdMode;

    private AdAdapted(Context context, String appId, String[] zones, boolean isProdMode) {
        this.listeners = new HashSet<>();

        this.context = context;

        this.isProdMode = isProdMode;
        String sdkVersion = context.getString(R.string.sdk_version);

        ImageCache.getInstance().purgeCache();

        DeviceInfoBuilder builder = new DeviceInfoBuilder();
        builder.addListener(this);
        builder.execute(new BuildDeviceInfoParam(context, appId, zones, sdkVersion));
    }

    public static synchronized AdAdapted getInstance() {
        return instance;
    }

    public static synchronized void init(Context context, String appId, String[] zones, boolean prodMode) {
        if(instance == null) {
            Log.d(TAG, "init() called for appId: " + appId + " and zones: " + Arrays.toString(zones));
            instance = new AdAdapted(context, appId, zones, prodMode);
        }
    }

    public Context getContext() {
        return context;
    }

    public DeviceInfo getDeviceInfo() {
        return deviceInfo;
    }

    public boolean isProd() {
        return isProdMode;
    }

    private SessionManager getSessionManager() {
        return SessionManagerFactory.getInstance(context).createSessionManager();
    }

    public void addListener(AdAdapted.Listener listener) {
        listeners.add(listener);
    }

    public void removeListener(AdAdapted.Listener listener) {
        listeners.remove(listener);
    }

    @Override
    public void onDeviceInfoCollected(DeviceInfo deviceInfo) {
        this.deviceInfo = deviceInfo;
        getSessionManager().initialize(deviceInfo);
    }

    @Override
    public String toString() {
        return "AdAdapted{}";
    }
}
