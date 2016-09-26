package com.adadapted.android.sdk.ext.http;

import android.util.Log;

import com.adadapted.android.sdk.core.event.AdEventAdapter;
import com.adadapted.android.sdk.core.event.AdEventAdapterListener;
import com.adadapted.android.sdk.ext.factory.AnomalyTrackerFactory;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;

/**
 * Created by chrisweeden on 3/23/15.
 */
public class HttpAdEventAdapter implements AdEventAdapter {
    private static final String LOGTAG = HttpAdEventAdapter.class.getName();

    private final String mBatchUrl;

    public HttpAdEventAdapter(final String batchUrl) {
        mBatchUrl = batchUrl == null ? "" : batchUrl;
    }

    @Override
    public void sendBatch(final JSONArray json,
                          final AdEventAdapterListener listener) {
        if(json == null || listener == null) {
            return;
        }

        final JsonArrayRequest jsonRequest = new JsonArrayRequest(Request.Method.POST,
                mBatchUrl, json, new Response.Listener<JSONArray>(){

            @Override
            public void onResponse(JSONArray response) {
                listener.onSuccess();
            }

        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(LOGTAG, "Event Batch Request Failed.", error);
                AnomalyTrackerFactory.registerAnomaly("",
                        mBatchUrl,
                        "AD_EVENT_TRACK_REQUEST_FAILED",
                        error.getMessage());
                listener.onFailure(json);
            }

        });

        HttpRequestManager.getQueue().add(jsonRequest);
    }
}
