package com.adadapted.android.sdk.ext.http;

import android.util.Log;

import com.adadapted.android.sdk.core.ad.AdEventSink;
import com.adadapted.android.sdk.ext.management.AdAnomalyTrackingManager;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;

/**
 * Created by chrisweeden on 3/23/15.
 */
public class HttpAdEventSink implements AdEventSink {
    private static final String LOGTAG = HttpAdEventSink.class.getName();

    private final String mBatchUrl;

    public HttpAdEventSink(final String batchUrl) {
        mBatchUrl = batchUrl == null ? "" : batchUrl;
    }

    @Override
    public void sendBatch(final JSONArray json) {
        if(json == null) {
            return;
        }

        final JsonArrayRequest jsonRequest = new JsonArrayRequest(Request.Method.POST,
                mBatchUrl, json, new Response.Listener<JSONArray>(){

            @Override
            public void onResponse(JSONArray response) {
            }

        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(LOGTAG, "Ad Event Batch Request Failed.", error);
                AdAnomalyTrackingManager.registerAnomaly("",
                        mBatchUrl,
                        "AD_EVENT_TRACK_REQUEST_FAILED",
                        error.getMessage());
            }

        });

        HttpRequestManager.queueRequest(jsonRequest);
    }
}
