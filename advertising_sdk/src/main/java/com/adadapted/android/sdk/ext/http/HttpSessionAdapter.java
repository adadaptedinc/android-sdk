package com.adadapted.android.sdk.ext.http;

import android.util.Log;

import com.adadapted.android.sdk.core.session.SessionAdapter;
import com.adadapted.android.sdk.core.session.SessionBuilder;
import com.adadapted.android.sdk.core.session.model.Session;
import com.adadapted.android.sdk.ext.management.AdAnomalyTrackingManager;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

/**
 * Created by chrisweeden on 3/23/15.
 */
public class HttpSessionAdapter implements SessionAdapter {
    private static final String LOGTAG = HttpSessionAdapter.class.getName();

    private final String initUrl;
    private final SessionBuilder builder;

    public HttpSessionAdapter(final String initUrl,
                              final SessionBuilder builder) {
        this.initUrl = initUrl == null ? "" : initUrl;
        this.builder = builder;
    }

    @Override
    public void sendInit(final JSONObject json,
                         final SessionAdapter.Callback listener) {
        if(json == null || listener == null) {
            return;
        }

        final JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST,
                initUrl, json, new Response.Listener<JSONObject>(){

            @Override
            public void onResponse(final JSONObject response) {
                final Session session = builder.buildSession(response);
                listener.onSuccess(session);
            }

        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(LOGTAG, "Session Init Request Failed.", error);
                AdAnomalyTrackingManager.registerAnomaly("",
                        initUrl,
                        "SESSION_REQUEST_FAILED",
                        error.getMessage());
                listener.onFailure();
            }

        });

        HttpRequestManager.queueRequest(jsonRequest);
    }
}