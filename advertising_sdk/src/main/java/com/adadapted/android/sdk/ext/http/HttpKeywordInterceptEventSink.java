package com.adadapted.android.sdk.ext.http;

import android.util.Log;

import com.adadapted.android.sdk.core.keywordintercept.KeywordInterceptEventSink;
import com.adadapted.android.sdk.ext.management.AdAnomalyTrackingManager;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;

/**
 * Created by chrisweeden on 9/30/16.
 */

public class HttpKeywordInterceptEventSink implements KeywordInterceptEventSink {
    private static final String LOGTAG = HttpKeywordInterceptEventSink.class.getName();

    private final String endpoint;

    public HttpKeywordInterceptEventSink(final String endpoint) {
        this.endpoint = endpoint;
    }

    @Override
    public void sendBatch(final JSONArray json) {
        if(json == null) {
            return;
        }

        final JsonArrayRequest jsonRequest = new JsonArrayRequest(Request.Method.POST,
                endpoint, json, new Response.Listener<JSONArray>(){
            @Override
            public void onResponse(JSONArray jsonObject) {
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(LOGTAG, "KI Track Request Failed.");
                AdAnomalyTrackingManager.registerAnomaly("",
                        endpoint,
                        "KI_EVENT_REQUEST_FAILED",
                        error.getMessage());
            }
        });

        HttpRequestManager.queueRequest(jsonRequest);
    }
}
