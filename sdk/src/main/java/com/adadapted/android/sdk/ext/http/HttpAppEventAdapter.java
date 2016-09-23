package com.adadapted.android.sdk.ext.http;

import android.util.Log;

import com.adadapted.android.sdk.core.event.AppEventAdapter;
import com.adadapted.android.sdk.core.event.AppEventAdapterListener;
import com.adadapted.android.sdk.ext.factory.AnomalyTrackerFactory;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

/**
 * Created by chrisweeden on 9/16/16.
 */
public class HttpAppEventAdapter implements AppEventAdapter {
    private static final String LOGTAG = HttpAppEventAdapter.class.getName();

    private final String mBatchUrl;

    public HttpAppEventAdapter(final String mBatchUrl) {
        this.mBatchUrl = mBatchUrl;
    }

    @Override
    public void sendBatch(final JSONObject json,
                          final AppEventAdapterListener listener) {
        if(json == null || listener == null) {
            return;
        }

        final JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST,
                mBatchUrl, json, new Response.Listener<JSONObject>(){
            @Override
            public void onResponse(JSONObject response) {
                listener.onSuccess();
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(LOGTAG, "Event Batch Request Failed.", error);
                AnomalyTrackerFactory.registerAnomaly("",
                        mBatchUrl,
                        "APP_EVENT_REQUEST_FAILED",
                        error.getMessage());
                //listener.onFailure(json);
            }
        });

        HttpRequestManager.getQueue().add(jsonRequest);
    }
}
