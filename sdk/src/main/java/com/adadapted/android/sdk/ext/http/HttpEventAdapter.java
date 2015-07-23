package com.adadapted.android.sdk.ext.http;

import android.util.Log;

import com.adadapted.android.sdk.core.event.EventAdapter;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by chrisweeden on 3/23/15.
 */
public class HttpEventAdapter implements EventAdapter {
    private static final String TAG = HttpEventAdapter.class.getName();

    private final Set<Listener> listeners;

    private final String batchUrl;

    public HttpEventAdapter(String batchUrl) {
        this.listeners = new HashSet<>();

        this.batchUrl = batchUrl;
    }

    @Override
    public void sendBatch(final JSONArray json) {
        JsonArrayRequest jsonRequest = new JsonArrayRequest(Request.Method.POST,
                batchUrl, json, new Response.Listener<JSONArray>(){

            @Override
            public void onResponse(JSONArray response) {
                notifyEventsPublished();
            }

        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, "Event Batch Request Failed.", error);
                notifyEventsFailed(json);
            }

        });

        HttpRequestManager.getQueue().add(jsonRequest);
    }

    @Override
    public void addListener(Listener listener) {
        this.listeners.add(listener);
    }

    @Override
    public void removeListener(Listener listener) {
        this.listeners.remove(listener);
    }

    @Override
    public void notifyEventsPublished() {
        for(Listener listener : listeners) {
            listener.onEventsPublished();
        }
    }

    @Override
    public void notifyEventsFailed(JSONArray json) {
        for(Listener listener : listeners) {
            listener.onEventsPublishFailed(json);
        }
    }
}
