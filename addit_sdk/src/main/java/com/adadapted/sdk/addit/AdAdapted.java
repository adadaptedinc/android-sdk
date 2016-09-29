package com.adadapted.sdk.addit;

import android.content.Context;
import android.util.Log;

import com.adadapted.sdk.addit.core.app.AppEventSource;
import com.adadapted.sdk.addit.config.Config;
import com.adadapted.sdk.addit.core.device.DeviceInfo;
import com.adadapted.sdk.addit.ext.factory.AppErrorTrackingManager;
import com.adadapted.sdk.addit.ext.factory.AppEventTrackingManager;
import com.adadapted.sdk.addit.ext.factory.DeviceInfoManager;
import com.adadapted.sdk.addit.ext.http.HttpRequestManager;
import com.adadapted.sdk.addit.ui.AdditContentListener;
import com.adadapted.sdk.addit.ui.AdditContentPublisher;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by chrisweeden on 9/26/16.
 */
public class AdAdapted {
    private static final String LOGTAG = AdAdapted.class.getName();

    public static class Env {
        public static final boolean PROD = true;
        public static final boolean DEV = false;
    }

    private static AdAdapted sInstance;

    public static AdAdapted init() {
        if(sInstance == null) {
            sInstance = new AdAdapted();
        }

        return sInstance;
    }

    private String mAppId = "";
    private boolean mIsProd = false;

    private AdAdapted() {}

    public AdAdapted withAppId(final String appId) {
        mAppId = (appId == null) ? "" : appId;

        return this;
    }

    public AdAdapted inEnv(final boolean isProd) {
        mIsProd = isProd;

        return this;
    }

    public AdAdapted setAdditContentListener(final AdditContentListener listener) {
        if(listener != null) {
            AdditContentPublisher.getInstance().addListener(listener);
        }
        else {
            Log.w(LOGTAG, "Cannot use NULL AdditContentListener");
        }

        return this;
    }

    public void start(final Context context) {
        if(context == null) {
            Log.e(LOGTAG, String.format("AdAdapted Android Addit SDK v%s failed to initialize with NULL Context.", Config.SDK_VERSION));
            return;
        }

        HttpRequestManager.createQueue(context);
        DeviceInfoManager.getInstance().collectDeviceInfo(context, mAppId, mIsProd, new DeviceInfoManager.Callback() {
            @Override
            public void onDeviceInfoCollected(final DeviceInfo deviceInfo) {
                AppEventTrackingManager.registerEvent(
                        AppEventSource.SDK,
                        "app_opened",
                        new HashMap<String, String>());

                if(!deviceInfo.isProd()) {
                    AppErrorTrackingManager.registerEvent(
                            "NOT_AN_ERROR",
                            "Error Collection Test Message. This message is only sent from the Dev environment.",
                            new HashMap<String, String>());
                }

                Log.i(LOGTAG, String.format("AdAdapted Android Addit SDK v%s initialized.", Config.SDK_VERSION));
            }
        });
    }

    public static void registerEvent(final String eventName,
                                     final Map<String, String> eventParams) {
        AppEventTrackingManager.registerEvent(AppEventSource.APP, eventName, eventParams);
    }

    public static void registerEvent(final String eventName) {
        registerEvent(eventName, new HashMap<String, String>());
    }
}
