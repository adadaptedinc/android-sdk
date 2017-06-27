package com.adadapted.android.sdk;

import android.content.Context;
import android.util.Log;

import com.adadapted.android.sdk.config.Config;
import com.adadapted.android.sdk.core.event.model.AppEventSource;
import com.adadapted.android.sdk.core.session.model.Session;
import com.adadapted.android.sdk.ext.cache.ImageCache;
import com.adadapted.android.sdk.ext.http.HttpRequestManager;
import com.adadapted.android.sdk.ext.management.AppErrorTrackingManager;
import com.adadapted.android.sdk.ext.management.AppEventTrackingManager;
import com.adadapted.android.sdk.ext.management.PayloadPickupManager;
import com.adadapted.android.sdk.ext.management.SessionManager;
import com.adadapted.android.sdk.ext.scheduler.EventFlushScheduler;
import com.adadapted.android.sdk.ui.messaging.AaSdkAdditContentListener;
import com.adadapted.android.sdk.ui.messaging.AaSdkEventListener;
import com.adadapted.android.sdk.ui.messaging.AaSdkSessionListener;
import com.adadapted.android.sdk.ui.messaging.AdditContentPublisher;
import com.adadapted.android.sdk.ui.messaging.SdkEventPublisher;

import java.util.HashMap;
import java.util.Map;

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
    private Map<String, String> mParams;
    private AaSdkSessionListener sessionListener;

    private AdAdapted() {
        mParams = new HashMap<>();
    }

    private synchronized static AdAdapted getsInstance() {
        if(sInstance == null) {
            sInstance = new AdAdapted();
        }

        return sInstance;
    }

    public static AdAdapted init() {
        return getsInstance();
    }

    public AdAdapted withAppId(final String appId) {
        if(appId == null) {
            Log.e(LOGTAG, "The Application Id cannot be Null.");
            mAppId = "";
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
        sessionListener = listener;

        return this;
    }

    public AdAdapted setSdkEventListener(final AaSdkEventListener listener) {
        SdkEventPublisher.getInstance().setListener(listener);

        return this;
    }

    public AdAdapted setSdkAdditContentListener(final AaSdkAdditContentListener listener) {
        AdditContentPublisher.getInstance().addListener(listener);

        return this;
    }

    public AdAdapted withParams(final Map<String, String> params) {
        mParams = params;

        return this;
    }

    public void start(final Context context) {
        ImageCache.getInstance().purgeCache();
        HttpRequestManager.createQueue(context.getApplicationContext());
        PayloadPickupManager.pickupPayloads();

        final SessionManager.Callback startCallback = new SessionManager.Callback() {
            @Override
            public void onSessionAvailable(final Session session) {
                if(sessionListener != null) {
                    sessionListener.onHasAdsToServe(session.hasActiveCampaigns());
                }
            }

            @Override
            public void onNewAdsAvailable(final Session session) {
                if(sessionListener != null) {
                    sessionListener.onHasAdsToServe(session.hasActiveCampaigns());
                }
            }
        };

        SessionManager.start(
            context.getApplicationContext(),
            mAppId,
            mIsProd,
            mParams,
            startCallback);

        AppEventTrackingManager.registerEvent(AppEventSource.SDK, "app_opened");

        if(!mIsProd) {
            AppErrorTrackingManager.registerEvent(
                "NOT_AN_ERROR",
                "Error Collection Test Message. This message is only sent from the Dev environment."
            );
        }

        new EventFlushScheduler().start(Config.DEFAULT_EVENT_POLLING);
        Log.i(LOGTAG, String.format("AdAdapted Android Advertising SDK v%s initialized.", Config.SDK_VERSION));
    }

    public static synchronized void restart(final Context context) {
        restart(context, new HashMap<String, String>());
    }

    public static synchronized void restart(final Context context,
                                            final Map<String, String> params) {
        SessionManager.restart(
            context.getApplicationContext(),
            getsInstance().mAppId,
            getsInstance().mIsProd,
            params
        );

        AppEventTrackingManager.registerEvent(
            AppEventSource.SDK,
            "session_restarted",
            params);

        Log.i(LOGTAG, String.format("AdAdapted Android Advertising SDK v%s reinitialized.", Config.SDK_VERSION));
    }

    public static synchronized void hasAdsToServe() {
        if(getsInstance().sessionListener != null) {
            final Session session = SessionManager.getCurrentSession();

            if(session != null) {
                getsInstance().sessionListener.onHasAdsToServe(session.hasActiveCampaigns());
            }
        }
    }

    public static synchronized void registerEvent(final String eventName) {
        registerEvent(eventName, new HashMap<String, String>());
    }

    public static synchronized void registerEvent(final String eventName,
                                                  final Map<String, String> eventParams) {
        AppEventTrackingManager.registerEvent(AppEventSource.APP, eventName, eventParams);
    }
}
