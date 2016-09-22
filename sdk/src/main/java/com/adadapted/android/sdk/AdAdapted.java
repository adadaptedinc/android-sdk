package com.adadapted.android.sdk;

import android.content.Context;
import android.util.Log;

import com.adadapted.android.sdk.addit.AaSdkAdditContentListener;
import com.adadapted.android.sdk.addit.AdditContentPublisher;
import com.adadapted.android.sdk.config.Config;
import com.adadapted.android.sdk.core.device.DeviceInfoBuilder;
import com.adadapted.android.sdk.core.device.model.DeviceInfo;
import com.adadapted.android.sdk.core.event.model.AppEventSource;
import com.adadapted.android.sdk.core.session.SessionListener;
import com.adadapted.android.sdk.core.session.SessionManager;
import com.adadapted.android.sdk.core.session.model.Session;
import com.adadapted.android.sdk.ext.cache.ImageCache;
import com.adadapted.android.sdk.ext.factory.AppEventTrackerFactory;
import com.adadapted.android.sdk.ext.factory.DeviceInfoFactory;
import com.adadapted.android.sdk.ext.factory.SessionManagerFactory;
import com.adadapted.android.sdk.ui.messaging.AaSdkEventListener;
import com.adadapted.android.sdk.ui.messaging.AaSdkSessionListener;
import com.adadapted.android.sdk.ui.messaging.SdkEventPublisher;
import com.adadapted.android.sdk.ui.messaging.SdkEventPublisherFactory;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class AdAdapted {
    private static final String LOGTAG = AdAdapted.class.getName();

    /**
     *
     */
    public static class Env {
        public static final boolean PROD = true;
        public static final boolean DEV = false;
    }

    private static AdAdapted sInstance;

    private String mAppId;
    private boolean mIsProd;

    private boolean mSdkStarting = false;
    private boolean mSdkLoaded = false;

    private final Context mContext;

    private AdAdapted(final Context context) {
        mContext = context;

        ImageCache.getInstance().purgeCache();
    }

    public static AdAdapted init(final Context context) {
        if(sInstance == null) {
            sInstance = new AdAdapted(context);
        }

        return sInstance;
    }

    public AdAdapted withAppId(final String appId) {
        if(appId == null) {
            Log.e(LOGTAG, "The Application Id cannot be Null.");
            mAppId = "";
            mSdkLoaded = true;
        }
        else {
            mAppId = appId;
        }

        return this;
    }

    public AdAdapted inEnv(final boolean environment) {
        mIsProd = environment;

        return this;
    }

    public AdAdapted setSdkSessionListener(final AaSdkSessionListener listener) {
        SessionManagerFactory.addListener(new SessionListener() {
            @Override
            public void onSessionInitialized(Session session) {
                Log.i(LOGTAG, String.format("AdAdapted Android SDK v%s initialized.", Config.SDK_VERSION));

                listener.onHasAdsToServe(session.hasActiveCampaigns());
            }

            @Override
            public void onSessionInitFailed() {
                Log.e(LOGTAG, String.format("AdAdapted Android SDK v%s failed to initialize.", Config.SDK_VERSION));
            }

            @Override
            public void onNewAdsAvailable(Session session) {
            }
        });

        return this;
    }

    public AdAdapted setSdkEventListener(final AaSdkEventListener listener) {
        getSdkEventPublisher().setListener(listener);

        return this;
    }

    public AdAdapted setSdkAdditContentListener(final AaSdkAdditContentListener listener) {
        AdditContentPublisher.getInstance().addListener(listener);

        return this;
    }

    public void start() {
        if(!mSdkStarting && !mSdkLoaded) {
            mSdkStarting = true;
            DeviceInfoFactory.createDeviceInfo(mContext, mAppId, Config.SDK_VERSION,
                    mIsProd, new DeviceInfoBuilder.Listener() {
                @Override
                public void onDeviceInfoCollected(DeviceInfo deviceInfo) {
                    getSessionManager().initialize(deviceInfo);

                    mSdkStarting = false;
                    mSdkLoaded = true;
                }
            });
        }
        else {
            Log.w(LOGTAG, "AdAdapted SDK has already been loaded with App Id: " + mAppId + ".");
        }
    }

    public static synchronized void registerEvent(final String eventName) {
        registerEvent(eventName, new HashMap<String, String>());
    }

    public static synchronized void registerEvent(final String eventName,
                                                  final Map<String, String> eventParams) {
        AppEventTrackerFactory.registerEvent(AppEventSource.APP, eventName, eventParams);
    }

    public boolean isLoaded() {
        return mSdkStarting || mSdkLoaded;
    }

    private SessionManager getSessionManager() {
        return SessionManagerFactory.createSessionManager(mContext);
    }

    private SdkEventPublisher getSdkEventPublisher() {
        return SdkEventPublisherFactory.getSdkEventPublisher();
    }
}
