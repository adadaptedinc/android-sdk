package com.adadapted.android.sdk.ext.http;

import android.util.Log;

import com.adadapted.android.sdk.core.anomaly.AnomalyAdapter;
import com.adadapted.android.sdk.core.anomaly.AnomalyAdapterListener;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;

/**
 * Created by chrisweeden on 9/23/16.
 */
public class HttpAnomalyAdapter implements AnomalyAdapter {
    private static final String LOGTAG = HttpAnomalyAdapter.class.getName();

    private final String mBatchUrl;

    public HttpAnomalyAdapter(String batchUrl) {
        this.mBatchUrl = batchUrl;
    }

    @Override
    public void sendBatch(final JSONArray json, final AnomalyAdapterListener listener) {
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
                Log.e(LOGTAG, "Event Batch Request Failed.", error);
                //listener.onFailure(json);
            }
        });

        HttpRequestManager.getQueue().add(jsonRequest);
    }
}
