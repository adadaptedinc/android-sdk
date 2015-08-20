package com.adadapted.android.sdk;

import android.content.Context;
import android.util.Log;

import com.adadapted.android.sdk.config.Config;
import com.adadapted.android.sdk.core.device.DeviceInfoBuilder;
import com.adadapted.android.sdk.core.device.model.DeviceInfo;
import com.adadapted.android.sdk.core.session.SessionManager;
import com.adadapted.android.sdk.ext.cache.ImageCache;
import com.adadapted.android.sdk.ext.factory.DeviceInfoFactory;
import com.adadapted.android.sdk.ext.factory.SessionManagerFactory;
import com.adadapted.android.sdk.ui.messaging.AaSdkEventListener;
import com.adadapted.android.sdk.ui.messaging.SdkEventPublisher;
import com.adadapted.android.sdk.ui.messaging.SdkEventPublisherFactory;

/**
 * Created by chrisweeden on 8/18/15.
 */
public class AdAdapted {
    private static final String LOGTAG = AdAdapted.class.getName();

    public static class Env {
        public static final boolean PROD = true;
        public static final boolean DEV = false;
    }

    private static AdAdapted sInstance;

    private String mAppId;
    private boolean mIsProd;

    private boolean mSdkLoaded = false;

    private Context mContext;

    private AdAdapted(Context context) {
        mContext = context;

        ImageCache.getInstance().purgeCache();
    }

    public static AdAdapted init(Context context) {
        if(sInstance == null) {
            sInstance = new AdAdapted(context);
        }

        return sInstance;
    }

    public AdAdapted withAppId(String appId) {
        mAppId = appId;

        return this;
    }

    public AdAdapted inEnv(boolean environment) {
        mIsProd = environment;

        return this;
    }

    public AdAdapted setSdkEventListener(AaSdkEventListener listener) {
        getSdkEventPublisher().setListener(listener);

        return this;
    }

    public void start() {
        if(!mSdkLoaded) {
            DeviceInfoFactory.createDeviceInfo(mContext, mAppId, Config.SDK_VERSION,
                    mIsProd, new DeviceInfoBuilder.Listener() {
                @Override
                public void onDeviceInfoCollected(DeviceInfo deviceInfo) {
                    getSessionManager().initialize(deviceInfo);
                    mSdkLoaded = true;
                }
            });
        }
        else {
            Log.w(LOGTAG, "AdAdapted SDK has already been loaded with App Id: " + mAppId + ".");
        }
    }

    private SessionManager getSessionManager() {
        return SessionManagerFactory.createSessionManager(mContext);
    }

    private SdkEventPublisher getSdkEventPublisher() {
        return SdkEventPublisherFactory.getSdkEventPublisher();
    }
}
