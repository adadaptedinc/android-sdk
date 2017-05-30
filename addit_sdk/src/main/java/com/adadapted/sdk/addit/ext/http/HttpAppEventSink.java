package com.adadapted.sdk.addit.ext.http;

import android.util.Log;

import com.adadapted.sdk.addit.core.app.AppEventSink;
import com.adadapted.sdk.addit.ext.management.AppErrorTrackingManager;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

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
            public void onResponse(final JSONObject response) {}

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(final VolleyError error) {
                Log.e(LOGTAG, "App Event Request Failed.", error);

                if(error instanceof NoConnectionError || error instanceof NetworkError) {
                    return;
                }

                final Map<String, String> errorParams = new HashMap<>();
                errorParams.put("endpoint", endpoint);
                errorParams.put("exception", error.getClass().getName());

                AppErrorTrackingManager.registerEvent(
                        "APP_EVENT_REQUEST_FAILED",
                        error.getMessage(),
                        errorParams);
            }
        });
        jsonRequest.setRetryPolicy(new DefaultRetryPolicy(1000 * 20, 2, 1.0f));

        HttpRequestManager.getQueue().add(jsonRequest);
    }
}
