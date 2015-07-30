package com.adadapted.android.sdk.ext.factory;

import android.content.Context;

import com.adadapted.android.sdk.AdAdapted;
import com.adadapted.android.sdk.config.Config;
import com.adadapted.android.sdk.core.session.SessionBuilder;
import com.adadapted.android.sdk.core.session.SessionManager;
import com.adadapted.android.sdk.core.session.SessionRequestBuilder;
import com.adadapted.android.sdk.ext.http.HttpSessionAdapter;
import com.adadapted.android.sdk.ext.json.JsonSessionBuilder;
import com.adadapted.android.sdk.ext.json.JsonSessionRequestBuilder;

/**
 * Created by chrisweeden on 5/26/15.
 */
public class SessionManagerFactory {
    private static SessionManagerFactory instance;

    public static synchronized SessionManagerFactory getInstance() {
        if(instance == null) {
            instance = new SessionManagerFactory();
        }

        return instance;
    }

    private SessionManager sessionManager;

    private SessionManagerFactory() {}

    public SessionManager createSessionManager(Context context) {
        if(sessionManager == null) {
            HttpSessionAdapter adapter = new HttpSessionAdapter(determineInitEndpoint(),
                    determineReinitEndpoint());
            SessionRequestBuilder requestBuilder = new JsonSessionRequestBuilder();
            SessionBuilder sessionBuilder = new JsonSessionBuilder(context);

            sessionManager = new SessionManager(context, adapter, requestBuilder, sessionBuilder);

            AdFetcherFactory.getInstance().createAdFetcher(context).addListener(sessionManager);
        }

        return sessionManager;
    }

    private String determineInitEndpoint() {
        if(AdAdapted.getInstance().isProd()) {
            return Config.Prod.URL_SESSION_INIT;
        }

        return Config.Sand.URL_SESSION_INIT;
    }

    private String determineReinitEndpoint() {
        if(AdAdapted.getInstance().isProd()) {
            return Config.Prod.URL_SESSION_REINIT;
        }

        return Config.Sand.URL_SESSION_REINIT;
    }
}
