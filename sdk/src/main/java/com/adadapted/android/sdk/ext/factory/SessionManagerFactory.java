package com.adadapted.android.sdk.ext.factory;

import android.content.Context;

import com.adadapted.android.sdk.AdAdapted;
import com.adadapted.android.sdk.R;
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

    public static synchronized SessionManagerFactory getInstance(Context context) {
        if(instance == null) {
            instance = new SessionManagerFactory(context);
        }

        return instance;
    }

    private final Context context;
    private SessionManager sessionManager;

    private SessionManagerFactory(Context context) {
        this.context = context;
    }

    public SessionManager createSessionManager() {
        if(sessionManager == null) {
            HttpSessionAdapter adapter = new HttpSessionAdapter(determineInitEndpoint(),
                    determineReinitEndpoint());
            SessionRequestBuilder requestBuilder = new JsonSessionRequestBuilder();
            SessionBuilder sessionBuilder = new JsonSessionBuilder();

            sessionManager = new SessionManager(context, adapter, requestBuilder, sessionBuilder);
        }

        return sessionManager;
    }

    private String determineInitEndpoint() {
        if(AdAdapted.getInstance().isProd()) {
            return context.getString(R.string.prod_session_init_object_url);
        }

        return context.getString(R.string.sandbox_session_init_object_url);
    }

    private String determineReinitEndpoint() {
        if(AdAdapted.getInstance().isProd()) {
            return context.getString(R.string.prod_session_reinit_object_url);
        }

        return context.getString(R.string.sandbox_session_reinit_object_url);
    }
}
