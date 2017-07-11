package com.adadapted.android.sdk;

import android.content.Context;
import android.util.Log;

import com.adadapted.android.sdk.config.Config;
import com.adadapted.android.sdk.core.addit.PayloadClient;
import com.adadapted.android.sdk.core.event.AppEventClient;
import com.adadapted.android.sdk.core.session.Session;
import com.adadapted.android.sdk.core.session.SessionClient;
import com.adadapted.android.sdk.ext.Wireup;
import com.adadapted.android.sdk.ext.http.HttpRequestManager;
import com.adadapted.android.sdk.ui.messaging.AaSdkAdditContentListener;
import com.adadapted.android.sdk.ui.messaging.AaSdkEventListener;
import com.adadapted.android.sdk.ui.messaging.AaSdkSessionListener;
import com.adadapted.android.sdk.ui.messaging.AdditContentPublisher;
import com.adadapted.android.sdk.ui.messaging.SdkEventPublisher;

import java.util.HashMap;
import java.util.Map;

public class AdAdapted {
    private static final String LOGTAG = AdAdapted.class.getName();

    public static class Env {
        public static final boolean PROD = true;
        public static final boolean DEV = false;
    }

    private static AdAdapted sInstance;

    private String appId;
    private boolean isProd;
    private Map<String, String> params;
    private AaSdkSessionListener sessionListener;

    private AdAdapted() {
        params = new HashMap<>();
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
            this.appId = "";
        }
        else {
            this.appId = appId;
        }

        return this;
    }

    public AdAdapted inEnv(final boolean environment) {
        isProd = environment;

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
        this.params = params;

        return this;
    }

    public void start(final Context context) {
        HttpRequestManager.createQueue(context.getApplicationContext());
        Wireup.run(isProd);

        PayloadClient.pickupPayloads();

        final SessionClient.Listener startListener = new SessionClient.Listener() {
            @Override
            public void onSessionAvailable(final Session session) {
                if(sessionListener != null) {
                    sessionListener.onHasAdsToServe(session.hasActiveCampaigns());
                }
            }

            @Override
            public void onAdsAvailable(Session session) {
                if(sessionListener != null) {
                    sessionListener.onHasAdsToServe(session.hasActiveCampaigns());
                }
            }

            @Override
            public void onSessionInitFailed() {
                sessionListener.onHasAdsToServe(false);
            }
        };

        SessionClient.start(
            context.getApplicationContext(),
                appId,
                isProd,
                params,
            startListener);

        AppEventClient.trackSdkEvent("app_opened");

        if(!isProd) {
            AppEventClient.trackError(
                "NOT_AN_ERROR",
                "Error Collection Test Message. This message is only sent from the Dev environment."
            );
        }

        //new EventFlushScheduler().start(Config.DEFAULT_EVENT_POLLING);
        Log.i(LOGTAG, String.format("AdAdapted Android Advertising SDK v%s initialized.", Config.SDK_VERSION));
    }

    public static synchronized void restart(final Context context) {
        restart(context, new HashMap<String, String>());
    }

    public static synchronized void restart(final Context context,
                                            final Map<String, String> params) {
        SessionClient.restart(
            context.getApplicationContext(),
            getsInstance().appId,
            getsInstance().isProd,
            params
        );

        AppEventClient.trackSdkEvent("session_restarted", params);

        Log.i(LOGTAG, String.format("AdAdapted Android Advertising SDK v%s reinitialized.", Config.SDK_VERSION));
    }

    public static synchronized void hasAdsToServe() {
        if(getsInstance().sessionListener != null) {
            final Session session = SessionClient.getCurrentSession();

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
        AppEventClient.trackAppEvent(eventName, eventParams);
    }
}
