package com.adadapted.android.sdk.ext.http;

import android.util.Log;

import com.adadapted.android.sdk.core.event.AppEventClient;
import com.adadapted.android.sdk.core.event.AppEventSink;
import com.adadapted.android.sdk.ext.json.JsonAppErrorBuilder;
import com.adadapted.android.sdk.ext.json.JsonAppEventBuilder;
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

    private final String eventUrl;
    private final String errorUrl;

    private final JsonAppEventBuilder eventBuilder;
    private final JsonAppErrorBuilder errorBuilder;

    public HttpAppEventSink(final String eventUrl,
                            final String errorUrl) {
        this.eventUrl = eventUrl;
        this.errorUrl = errorUrl;

        eventBuilder = new JsonAppEventBuilder();
        errorBuilder = new JsonAppErrorBuilder();
    }

    @Override
    public void publishEvent(final String type,
                             final String name,
                             final Map<String, String> params) {
        final JSONObject json = eventBuilder.buildItem(new JSONObject(), type, name, params);

        final JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST,
                eventUrl, json, new Response.Listener<JSONObject>(){
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
                errorParams.put("endpoint", eventUrl);
                AppEventClient.trackError(
                    "APP_EVENT_REQUEST_FAILED",
                    error.getMessage(),
                    errorParams
                );
            }
        });

        HttpRequestManager.queueRequest(jsonRequest);
    }

    @Override
    public void publishError(final String code,
                             final String message,
                             final Map<String, String> params) {
        final JSONObject json = errorBuilder.buildItem(new JSONObject(), code, message, params);

        final JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST,
                errorUrl, json, new Response.Listener<JSONObject>(){
            @Override
            public void onResponse(JSONObject response) {}

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(LOGTAG, "App Error Request Failed.", error);
            }
        });

        HttpRequestManager.queueRequest(jsonRequest);
    }
}
