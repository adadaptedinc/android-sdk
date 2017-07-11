package com.adadapted.android.sdk.ext.http;

import android.util.Log;

import com.adadapted.android.sdk.core.event.AppEventClient;
import com.adadapted.android.sdk.core.keywordintercept.KeywordInterceptEventSink;
import com.adadapted.android.sdk.core.keywordintercept.KeywordInterceptEvent;
import com.adadapted.android.sdk.ext.json.JsonKeywordInterceptEventBuilder;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class HttpKeywordInterceptEventSink implements KeywordInterceptEventSink {
    private static final String LOGTAG = HttpKeywordInterceptEventSink.class.getName();

    private final String endpoint;
    private final JsonKeywordInterceptEventBuilder builder;

    public HttpKeywordInterceptEventSink(final String endpoint) {
        this.endpoint = endpoint;
        this.builder = new JsonKeywordInterceptEventBuilder();
    }

    @Override
    public void sendBatch(final Set<KeywordInterceptEvent> events) {
        final JSONArray json = builder.buildEvents(events);

        final JsonArrayRequest jsonRequest = new JsonArrayRequest(Request.Method.POST,
                endpoint, json, new Response.Listener<JSONArray>(){
            @Override
            public void onResponse(JSONArray jsonObject) {
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(LOGTAG, "KI Track Request Failed.");

                if(error instanceof NoConnectionError || error instanceof NetworkError) {
                    return;
                }

                final Map<String, String> params = new HashMap<>();
                params.put("url", endpoint);
                AppEventClient.trackError(
                    "KI_EVENT_REQUEST_FAILED",
                    error.getMessage(),
                    params
                );
            }
        });

        HttpRequestManager.queueRequest(jsonRequest);
    }
}
