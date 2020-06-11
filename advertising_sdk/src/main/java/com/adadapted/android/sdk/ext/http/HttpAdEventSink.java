package com.adadapted.android.sdk.ext.http;

import com.adadapted.android.sdk.core.ad.AdEvent;
import com.adadapted.android.sdk.core.ad.AdEventSink;
import com.adadapted.android.sdk.core.event.AppEventClient;
import com.adadapted.android.sdk.core.session.Session;
import com.adadapted.android.sdk.ext.json.JsonAdEventBuilder;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class HttpAdEventSink implements AdEventSink {
    @SuppressWarnings("unused")
    private static final String LOGTAG = HttpAdEventSink.class.getName();

    private final String batchUrl;
    private final JsonAdEventBuilder builder;

    public HttpAdEventSink(final String batchUrl) {
        this.batchUrl = batchUrl == null ? "" : batchUrl;
        builder = new JsonAdEventBuilder();
    }

    @Override
    public void sendBatch(final Session session, final Set<AdEvent> events) {
        final JSONObject json = builder.marshalEvents(session, events);

        final JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST,
                batchUrl, json, new Response.Listener<JSONObject>(){

            @Override
            public void onResponse(JSONObject response) {}

        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if(error != null && error.networkResponse != null) {
                    final int statusCode = error.networkResponse.statusCode;

                    if(statusCode >= 400) {
                        final String data = new String(error.networkResponse.data);

                        final Map<String, String> params = new HashMap<>();
                        params.put("url", batchUrl);
                        params.put("status_code", Integer.toString(statusCode));
                        params.put("data", data);
                        AppEventClient.Companion.getInstance().trackError(
                                "AD_EVENT_TRACK_REQUEST_FAILED",
                                error.getMessage(),
                                params
                        );
                    }
                }
            }

        });

        HttpRequestManager.queueRequest(jsonRequest);
    }
}
