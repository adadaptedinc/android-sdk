package com.adadapted.android.sdk;

import android.content.Context;
import android.util.Log;

import com.adadapted.android.sdk.core.device.BuildDeviceInfoParam;
import com.adadapted.android.sdk.core.device.model.DeviceInfo;
import com.adadapted.android.sdk.core.device.DeviceInfoBuilder;
import com.adadapted.android.sdk.core.session.SessionManager;
import com.adadapted.android.sdk.ext.cache.ImageCache;
import com.adadapted.android.sdk.ext.factory.SessionManagerFactory;
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

    public interface AdEventListener {
        void onAdImpression(String zoneId);
        void onAdClick(String zoneId);
    }

    public interface ContentListener {
        void onContentAvailable(String zoneId, ContentPayload contentPayload);
    }

    public interface DelegateListener {
        void onDelegateAvailable(String zoneId, String delegate);
    }

    private final Set<AdEventListener> adEventListeners;
    private final Set<ContentListener> contentListeners;
    private final Set<DelegateListener> delegateListeners;

    private final Context context;
    private final boolean isProdMode;

    private DeviceInfo deviceInfo;

    private AdAdapted(Context context, String appId, String[] zones, boolean isProdMode) {
        this.adEventListeners = new HashSet<>();
        this.contentListeners = new HashSet<>();
        this.delegateListeners = new HashSet<>();

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

    public static synchronized void addAdListener(AdEventListener listener) {
        getInstance().addListener(listener);
    }

    public static synchronized void removeAdListener(AdEventListener listener) {
        getInstance().removeListener(listener);
    }

    public static synchronized void addContentListener(ContentListener listener) {
        getInstance().addListener(listener);
    }

    public static synchronized void removeContentListener(ContentListener listener) {
        getInstance().removeListener(listener);
    }

    public static synchronized void addDelegateListener(DelegateListener listener) {
        getInstance().addListener(listener);
    }

    public static synchronized void removeDelegateListener(DelegateListener listener) {
        getInstance().removeListener(listener);
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

    public void addListener(AdEventListener listener) {
        adEventListeners.add(listener);
    }

    public void addListener(ContentListener listener) {
        contentListeners.add(listener);
    }

    public void addListener(DelegateListener listener) {
        delegateListeners.add(listener);
    }

    public void removeListener(AdEventListener listener) {
        adEventListeners.remove(listener);
    }

    public void removeListener(ContentListener listener) {
        contentListeners.remove(listener);
    }

    public void removeListener(DelegateListener listener) {
        delegateListeners.remove(listener);
    }

    public void publishAdImpression(String zoneId) {
        for(AdEventListener listener : adEventListeners) {
            listener.onAdImpression(zoneId);
        }
    }

    public void publishAdClick(String zoneId) {
        for(AdEventListener listener : adEventListeners) {
            listener.onAdClick(zoneId);
        }
    }

    public void publishContent(String zoneId, ContentPayload payload) {
        for(ContentListener listener : contentListeners) {
            listener.onContentAvailable(zoneId, payload);
        }
    }

    public void publishDelegate(String zoneId, String delegate) {
        for(DelegateListener listener : delegateListeners) {
            listener.onDelegateAvailable(zoneId, delegate);
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
