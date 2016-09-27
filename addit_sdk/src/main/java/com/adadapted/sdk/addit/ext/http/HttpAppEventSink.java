package com.adadapted.sdk.addit.ext.http;

import android.util.Log;

import com.adadapted.sdk.addit.core.app.AppEventSink;
import com.adadapted.sdk.addit.ext.factory.AnomalyTrackingManager;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

/**
 * Created by chrisweeden on 9/26/16.
 */

public class HttpAppEventSink implements AppEventSink {
    private static final String LOGTAG = HttpAppEventSink.class.getName();

    private final String endpoint;

    public HttpAppEventSink(final String endpoint) {
        this.endpoint = endpoint;
    }

    @Override
    public void publishEvent(final JSONObject json) {
        if(json == null) {
            return;
        }

        final JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST,
                endpoint, json, new Response.Listener<JSONObject>(){
            @Override
            public void onResponse(JSONObject response) {}

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(LOGTAG, "App Event Request Failed.", error);
                AnomalyTrackingManager.registerAnomaly(
                        endpoint,
                        "APP_EVENT_REQUEST_FAILED",
                        error.getMessage());
            }
        });

        HttpRequestManager.getQueue().add(jsonRequest);
    }
}
