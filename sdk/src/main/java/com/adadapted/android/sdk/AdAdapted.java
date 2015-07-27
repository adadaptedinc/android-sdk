package com.adadapted.android.sdk;

import android.content.Context;
import android.util.Log;

import com.adadapted.android.sdk.config.Config;
import com.adadapted.android.sdk.core.device.BuildDeviceInfoParam;
import com.adadapted.android.sdk.core.device.model.DeviceInfo;
import com.adadapted.android.sdk.core.device.DeviceInfoBuilder;
import com.adadapted.android.sdk.core.session.SessionManager;
import com.adadapted.android.sdk.ext.cache.ImageCache;
import com.adadapted.android.sdk.ext.factory.SessionManagerFactory;
import com.adadapted.android.sdk.ui.listener.AaAdEventListener;
import com.adadapted.android.sdk.ui.listener.AaContentListener;
import com.adadapted.android.sdk.ui.model.ContentPayload;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by chrisweeden on 3/16/15.
 */
public class AdAdapted implements DeviceInfoBuilder.Listener {
    private static final String TAG = AdAdapted.class.getName();

    private static AdAdapted instance;

    private final Set<AaAdEventListener> adEventListeners;
    private final Set<AaContentListener> contentListeners;

    private final Context context;
    private final boolean isProdMode;

    private DeviceInfo deviceInfo;

    private AdAdapted(Context context, String appId, String[] zones, boolean isProdMode) {
        this.adEventListeners = new HashSet<>();
        this.contentListeners = new HashSet<>();

        this.context = context;

        this.isProdMode = isProdMode;

        ImageCache.getInstance().purgeCache();

        DeviceInfoBuilder builder = new DeviceInfoBuilder();
        builder.addListener(this);
        builder.execute(new BuildDeviceInfoParam(context, appId, zones, Config.SDK_VERSION));
    }

    public static synchronized AdAdapted getInstance() {
        return instance;
    }

    public static synchronized void init(Context context, String appId, String[] zones,
                                         boolean prodMode) {
        if(instance == null) {
            Log.d(TAG, "init() called for appId: " + appId + " and zones: " + Arrays.toString(zones));
            instance = new AdAdapted(context, appId, zones, prodMode);
        }
    }

    public static synchronized void init(Context context, String appId, String[] zones,
                                         boolean prodMode, AaAdEventListener listenter) {
        if(instance == null) {
            Log.d(TAG, "init() called for appId: " + appId + " and zones: " + Arrays.toString(zones));
            instance = new AdAdapted(context, appId, zones, prodMode);
            instance.addListener(listenter);
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
        return SessionManagerFactory.getInstance().createSessionManager(context);
    }

    public void addListener(AaAdEventListener listener) {
        adEventListeners.add(listener);
    }

    public void addListener(AaContentListener listener) {
        contentListeners.add(listener);
    }

    public void removeListener(AaAdEventListener listener) {
        adEventListeners.remove(listener);
    }

    public void removeListener(AaContentListener listener) {
        contentListeners.remove(listener);
    }

    public void publishAdImpression(String zoneId) {
        for(AaAdEventListener listener : adEventListeners) {
            listener.onAdImpression(zoneId);
        }
    }

    public void publishAdClick(String zoneId) {
        for(AaAdEventListener listener : adEventListeners) {
            listener.onAdClick(zoneId);
        }
    }

    public void publishContent(String zoneId, ContentPayload payload) {
        for(AaContentListener listener : contentListeners) {
            listener.onContentAvailable(zoneId, payload);
        }
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
