package com.adadapted.android.sdk.ext.http;

import android.util.Log;

import com.adadapted.android.sdk.core.event.AppEventSink;
import com.adadapted.android.sdk.ext.management.AppErrorTrackingManager;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

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

                if(error instanceof NoConnectionError || error instanceof NetworkError) {
                    return;
                }

                final Map<String, String> errorParams = new HashMap<>();
                errorParams.put("endpoint", endpoint);
                AppErrorTrackingManager.registerEvent(
                    "APP_EVENT_REQUEST_FAILED",
                    error.getMessage(),
                    errorParams
                );
            }
        });

        HttpRequestManager.queueRequest(jsonRequest);
    }
}
