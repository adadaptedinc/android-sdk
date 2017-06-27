package com.adadapted.android.sdk.ext.http;

import android.util.Log;

import com.adadapted.android.sdk.core.event.AppErrorSink;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

public class HttpAppErrorSink implements AppErrorSink {
    private static final String LOGTAG = HttpAppErrorSink.class.getName();

    private final String endpoint;

    public HttpAppErrorSink(final String endpoint) {
        this.endpoint = endpoint;
    }

    @Override
    public void publishError(final JSONObject json) {
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
                Log.e(LOGTAG, "App Error Request Failed.", error);
            }
        });

        HttpRequestManager.queueRequest(jsonRequest);
    }
}
