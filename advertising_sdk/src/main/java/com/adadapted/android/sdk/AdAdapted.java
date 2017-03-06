package com.adadapted.android.sdk;

import android.content.Context;
import android.util.Log;

import com.adadapted.android.sdk.config.Config;
import com.adadapted.android.sdk.core.event.model.AppEventSource;
import com.adadapted.android.sdk.core.session.model.Session;
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
    private AaSdkSessionListener sessionListener;

    private AdAdapted() {}

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

    public void start(final Context context) {
        SessionManager.start(context, mAppId, mIsProd, new SessionManager.Callback() {
            @Override
            public void onSessionAvailable(final Session session) {
                if(!session.getDeviceInfo().isProd()) {
                    AppErrorTrackingManager.registerEvent(
                            "NOT_AN_ERROR",
                            "Error Collection Test Message. This message is only sent from the Dev environment.",
                            new HashMap<String, String>());
                }

                if(sessionListener != null) {
                    sessionListener.onHasAdsToServe(session.hasActiveCampaigns());
                }

                PayloadPickupManager.pickupPayloads(session.getDeviceInfo());
            }

            @Override
            public void onNewAdsAvailable(final Session session) {
                if(sessionListener != null) {
                    sessionListener.onHasAdsToServe(session.hasActiveCampaigns());
                }
            }
        });

        AppEventTrackingManager.registerEvent(
                AppEventSource.SDK,
                "app_opened",
                new HashMap<String, String>());

        new EventFlushScheduler().start(Config.DEFAULT_EVENT_POLLING);
        Log.i(LOGTAG, String.format("AdAdapted Android Advertising SDK v%s initialized.", Config.SDK_VERSION));
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
