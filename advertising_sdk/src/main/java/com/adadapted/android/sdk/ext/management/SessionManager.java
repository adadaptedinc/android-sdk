package com.adadapted.android.sdk.ext.management;

import android.content.Context;
import android.util.Log;

import com.adadapted.android.sdk.config.Config;
import com.adadapted.android.sdk.core.ad.AdRefreshAdapter;
import com.adadapted.android.sdk.core.ad.RefreshAdsCommand;
import com.adadapted.android.sdk.core.ad.RefreshAdsInteractor;
import com.adadapted.android.sdk.core.common.Interactor;
import com.adadapted.android.sdk.core.device.DeviceInfo;
import com.adadapted.android.sdk.core.session.InitializeSessionCommand;
import com.adadapted.android.sdk.core.session.InitializeSessionInteractor;
import com.adadapted.android.sdk.core.session.SessionAdapter;
import com.adadapted.android.sdk.core.session.model.Session;
import com.adadapted.android.sdk.core.zone.model.Zone;
import com.adadapted.android.sdk.ext.cache.ImageCache;
import com.adadapted.android.sdk.ext.concurrency.ThreadPoolInteractorExecuter;
import com.adadapted.android.sdk.ext.http.HttpAdRefreshAdapter;
import com.adadapted.android.sdk.ext.http.HttpRequestManager;
import com.adadapted.android.sdk.ext.http.HttpSessionAdapter;
import com.adadapted.android.sdk.ext.json.JsonAdRefreshBuilder;
import com.adadapted.android.sdk.ext.json.JsonAdRequestBuilder;
import com.adadapted.android.sdk.ext.json.JsonSessionBuilder;
import com.adadapted.android.sdk.ext.json.JsonSessionRequestBuilder;
import com.adadapted.android.sdk.ext.json.JsonZoneBuilder;
import com.adadapted.android.sdk.ext.scheduler.AdRefreshScheduler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by chrisweeden on 9/30/16.
 */
public class SessionManager
        implements DeviceInfoManager.Callback,
        InitializeSessionInteractor.Callback,
        RefreshAdsInteractor.Callback {
    private static final String LOGTAG = SessionManager.class.getName();

    private static SessionManager sInstance;
    private static Session sSession;

    public static synchronized void start(final Context context,
                                          final String appId,
                                          final boolean isProd,
                                          final Callback callback) {
        if(sInstance == null) {
            sInstance = new SessionManager(context, appId, isProd, callback);
        }
    }

    public static synchronized void getSession(final Callback callback) {
        if(sInstance != null) {
            sInstance.addCallback(callback);
        }
        else {
            Log.w(LOGTAG, "Session Manager has not been started.");
        }
    }

    public static synchronized void refreshAds() {
        if(sInstance != null) {
            sInstance.performRefreshAds();
        }
        else {
            Log.w(LOGTAG, "Session Manager has not been started.");
        }
    }

    public static synchronized void reinitializeSession() {
        final Callback reinitSessionCallback = new Callback() {
            @Override
            public void onSessionAvailable(final Session session) {
                removeCallback(this);

                sInstance.initializeSession(session.getDeviceInfo());
            }

            @Override
            public void onNewAdsAvailable(Session session) {}
        };

        getSession(reinitSessionCallback);
    }

    public static synchronized void removeCallback(final Callback callback) {
        if(sInstance != null) {
            sInstance.callbacks.remove(callback);
        }
        else {
            Log.w(LOGTAG, "Session Manager has not been started.");
        }
    }

    private final Set<Callback> callbacks = new HashSet<>();

    private SessionAdapter sessionAdapter;
    private AdRefreshAdapter adRefreshAdapter;

    private SessionManager(final Context context,
                           final String appId,
                           final boolean isProd,
                           final Callback callback) {
        addCallback(callback);

        ImageCache.getInstance().purgeCache();
        HttpRequestManager.createQueue(context);
        DeviceInfoManager.getInstance().collectDeviceInfo(context, appId, isProd, this);
    }

    public static synchronized Session getCurrentSession() {
        return sSession;
    }

    @Override
    public void onDeviceInfoCollected(final DeviceInfo deviceInfo) {
        this.sessionAdapter = new HttpSessionAdapter(
                determineSessionEndpoint(deviceInfo),
                new JsonSessionBuilder(deviceInfo));

        adRefreshAdapter = new HttpAdRefreshAdapter(
                determineAdEndpoint(deviceInfo),
                new JsonAdRefreshBuilder(new JsonZoneBuilder(deviceInfo.getScale())));

        initializeSession(deviceInfo);
    }

    private void initializeSession(final DeviceInfo deviceInfo) {
        final InitializeSessionCommand command = new InitializeSessionCommand(
                deviceInfo,
                new JsonSessionRequestBuilder());
        final Interactor interactor = new InitializeSessionInteractor(command, sessionAdapter, this);

        ThreadPoolInteractorExecuter.getInstance().executeInBackground(interactor);
    }

    private String determineSessionEndpoint(final DeviceInfo deviceInfo) {
        if(deviceInfo.isProd()) {
            return Config.Prod.URL_SESSION_INIT;
        }

        return Config.Sand.URL_SESSION_INIT;
    }

    private String determineAdEndpoint(final DeviceInfo deviceInfo) {
        if(deviceInfo.isProd()) {
            return Config.Prod.URL_AD_GET;
        }

        return Config.Sand.URL_AD_GET;
    }

    private void addCallback(final Callback callback) {
        callbacks.add(callback);

        if(sSession != null) {
            callback.onSessionAvailable(sSession);
        }
    }

    private void performRefreshAds() {
        final RefreshAdsCommand command = new RefreshAdsCommand(sSession);
        final Interactor interactor = new RefreshAdsInteractor(
                command,
                adRefreshAdapter,
                new JsonAdRequestBuilder(),
                this);

        ThreadPoolInteractorExecuter.getInstance().executeInBackground(interactor);
    }

    @Override
    public void onSessionInitialized(final Session session) {
        sSession = session;
        new AdRefreshScheduler().schedule(session);

        notifyOnSessionAvailable(session);
    }

    @Override
    public void onAdRefreshSuccess(Map<String, Zone> zones) {
        final Session session = sSession.updateZones(zones);
        new AdRefreshScheduler().schedule(session);

        notifyOnNewAdsAvailable(session);
    }

    @Override
    public void onAdRefreshFailure() {
        final Session session = sSession.updateZones(new HashMap<String, Zone>());
        new AdRefreshScheduler().schedule(session);

        notifyOnNewAdsAvailable(session);
    }

    private void notifyOnSessionAvailable(final Session session) {
        final Set<Callback> currentCallbacks = new HashSet<>(callbacks);
        for(final Callback c : currentCallbacks) {
            c.onSessionAvailable(session);
        }
    }

    private void notifyOnNewAdsAvailable(final Session session) {
        sSession = session;

        Set<Callback> currentCallbacks = new HashSet<>(callbacks);
        for(final Callback c : currentCallbacks) {
            c.onNewAdsAvailable(session);
        }
    }

    public interface Callback {
        void onSessionAvailable(Session session);
        void onNewAdsAvailable(Session session);
    }
}
