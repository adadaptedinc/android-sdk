package com.adadapted.android.sdk.ext.factory;

import android.content.Context;

import com.adadapted.android.sdk.config.Config;
import com.adadapted.android.sdk.core.device.model.DeviceInfo;
import com.adadapted.android.sdk.core.session.SessionBuilder;
import com.adadapted.android.sdk.core.session.SessionListener;
import com.adadapted.android.sdk.core.session.SessionManager;
import com.adadapted.android.sdk.core.session.SessionRequestBuilder;
import com.adadapted.android.sdk.ext.http.HttpSessionAdapter;
import com.adadapted.android.sdk.ext.json.JsonSessionBuilder;
import com.adadapted.android.sdk.ext.json.JsonSessionRequestBuilder;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by chrisweeden on 5/26/15.
 */
public class SessionManagerFactory {
    private static SessionManagerFactory sInstance;
    private final static Set<SessionListener> sListeners = new HashSet<>();

    private final SessionManager mSessionManager;

    private SessionManagerFactory(final Context context, final DeviceInfo deviceInfo) {
        HttpSessionAdapter adapter = new HttpSessionAdapter(context, determineInitEndpoint(deviceInfo));
        SessionRequestBuilder requestBuilder = new JsonSessionRequestBuilder();
        SessionBuilder sessionBuilder = new JsonSessionBuilder(deviceInfo.getScale());

        mSessionManager = new SessionManager(context, adapter, requestBuilder, sessionBuilder);
        for(SessionListener listener : sListeners) {
            mSessionManager.addListener(listener);
        }
        sListeners.clear();

        AdFetcherFactory.createAdFetcher(deviceInfo).addListener(mSessionManager.getAdFetcherListener());
    }

    public static synchronized SessionManager createSessionManager(final Context context) {
        if(sInstance == null) {
            DeviceInfo deviceInfo = DeviceInfoFactory.getsDeviceInfo();
            if(deviceInfo != null) {
                sInstance = new SessionManagerFactory(context, deviceInfo);
            }
        }

        if(sInstance != null) {
            return sInstance.mSessionManager;
        }

        return null;
    }

    public static SessionManager getSessionManager() {
        if(sInstance != null) {
            return sInstance.mSessionManager;
        }

        return null;
    }

    public static void addListener(final SessionListener listener) {
        SessionManager manager = SessionManagerFactory.getSessionManager();
        if(manager != null) {
            manager.addListener(listener);
        }
        else {
            sListeners.add(listener);
        }
    }

    public static void removeListener(final SessionListener listener) {
        SessionManager manager = SessionManagerFactory.getSessionManager();
        if(manager != null) {
            manager.removeListener(listener);
        }
        else {
            sListeners.remove(listener);
        }
    }

    private String determineInitEndpoint(DeviceInfo deviceInfo) {
        if(deviceInfo.isProd()) {
            return Config.Prod.URL_SESSION_INIT;
        }

        return Config.Sand.URL_SESSION_INIT;
    }
}
