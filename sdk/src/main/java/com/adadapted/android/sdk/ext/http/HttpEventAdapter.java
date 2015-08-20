package com.adadapted.android.sdk.ext.http;

import android.util.Log;

import com.adadapted.android.sdk.core.event.EventAdapter;
import com.adadapted.android.sdk.core.event.EventAdapterListener;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;

/**
 * Created by chrisweeden on 3/23/15.
 */
public class HttpEventAdapter implements EventAdapter {
    private static final String LOGTAG = HttpEventAdapter.class.getName();

    private final String mBatchUrl;

    public HttpEventAdapter(final String batchUrl) {
        mBatchUrl = batchUrl;
    }

    @Override
    public void sendBatch(final JSONArray json, final EventAdapterListener listener) {
        JsonArrayRequest jsonRequest = new JsonArrayRequest(Request.Method.POST,
                mBatchUrl, json, new Response.Listener<JSONArray>(){

            @Override
            public void onResponse(JSONArray response) {
                listener.onSuccess();
            }

        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(LOGTAG, "Event Batch Request Failed.", error);
                listener.onFailure(json);
            }

        });

        HttpRequestManager.getQueue().add(jsonRequest);
    }
}
