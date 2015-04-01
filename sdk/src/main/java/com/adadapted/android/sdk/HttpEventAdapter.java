package com.adadapted.android.sdk;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by chrisweeden on 3/23/15.
 */
class HttpEventAdapter implements EventAdapter {
    private static final String TAG = HttpEventAdapter.class.getName();

    private Set<Listener> listeners;

    private String batchUrl;

    HttpEventAdapter(String batchUrl) {
        this.listeners = new HashSet<>();

        this.batchUrl = batchUrl;
    }

    @Override
    public void sendBatch(JSONArray json) throws SdkNotInitializedException {
        JsonArrayRequest jsonRequest = new JsonArrayRequest(Request.Method.POST,
                batchUrl, json, new Response.Listener<JSONArray>(){

            @Override
            public void onResponse(JSONArray response) {
                Log.d(TAG, "Event Batch Request Succeeded.");
                notifyEventsPublished();
            }

        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Event Batch Request Failed.", error);
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
}
