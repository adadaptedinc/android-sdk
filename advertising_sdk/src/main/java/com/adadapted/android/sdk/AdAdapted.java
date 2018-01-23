package com.adadapted.android.sdk;

import android.content.Context;
import android.util.Log;

import com.adadapted.android.sdk.config.Config;
import com.adadapted.android.sdk.core.addit.Content;
import com.adadapted.android.sdk.core.addit.PayloadClient;
import com.adadapted.android.sdk.core.event.AppEventClient;
import com.adadapted.android.sdk.core.session.Session;
import com.adadapted.android.sdk.core.session.SessionClient;
import com.adadapted.android.sdk.ext.Wireup;
import com.adadapted.android.sdk.ui.messaging.AaSdkAdditContentListener;
import com.adadapted.android.sdk.ui.messaging.AaSdkEventListener;
import com.adadapted.android.sdk.ui.messaging.AaSdkSessionListener;
import com.adadapted.android.sdk.ui.messaging.AdditContentPublisher;
import com.adadapted.android.sdk.ui.messaging.SdkEventPublisher;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdAdapted {
    private static final String LOGTAG = AdAdapted.class.getName();

    public static class Env {
        public static final boolean PROD = true;
        public static final boolean DEV = false;
    }

    private static AdAdapted sInstance;

    private boolean hasStarted;
    private String appId;
    private boolean isProd;
    private Map<String, String> params;
    private AaSdkSessionListener sessionListener;
    private AaSdkEventListener eventListener;
    private AaSdkAdditContentListener contentListener;

    private AdAdapted() {
        hasStarted = false;
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
        eventListener = listener;

        return this;
    }

    public AdAdapted setSdkAdditContentListener(final AaSdkAdditContentListener listener) {
        contentListener = listener;

        return this;
    }

    public AdAdapted withParams(final Map<String, String> params) {
        this.params = params;

        return this;
    }

    public void start(final Context context) {
        if(context == null) {
            Log.e(LOGTAG, "The Context cannot be NULL");
            return;
        }

        if(appId == null) {
            Log.e(LOGTAG, "The Application Id cannot be NULL");
            return;
        }

        if(hasStarted) {
            Log.w(LOGTAG, "AdAdapted Android Advertising SDK has already been started");
            return;
        }

        hasStarted = true;

        Wireup.run(context, isProd);

        SdkEventPublisher.getInstance().setListener(eventListener);
        AdditContentPublisher.getInstance().addListener(contentListener);

        PayloadClient.pickupPayloads(new PayloadClient.Callback() {
            @Override
            public void onPayloadAvailable(final List<Content> content) {
                if(content.size() > 0) {
                    AdditContentPublisher.getInstance().publishContent(content.get(0));
                }
            }
        });

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
                if(sessionListener != null) {
                    sessionListener.onHasAdsToServe(false);
                }
            }
        };

        SessionClient.start(
            context.getApplicationContext(),
            appId,
            isProd,
            params,
            startListener);

        AppEventClient.trackSdkEvent("app_opened");

        Log.i(LOGTAG, String.format("AdAdapted Android Advertising SDK v%s initialized.", Config.SDK_VERSION));
    }

    public static synchronized void restart(final Context context) {
        restart(context, new HashMap<String, String>());
    }

    public static synchronized void restart(final Context context,
                                            final Map<String, String> params) {
        if(context == null) {
            Log.e(LOGTAG, "The Context cannot be NULL");
            return;
        }

        if(getsInstance().appId == null) {
            Log.e(LOGTAG, "The Application Id cannot be Null.");
            return;
        }

        AppEventClient.trackSdkEvent("sdk_restarted");

        SessionClient.restart(
            context.getApplicationContext(),
            getsInstance().appId,
            getsInstance().isProd,
            params
        );

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
