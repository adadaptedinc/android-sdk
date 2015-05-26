package com.adadapted.android.sdk.ext.http;

import android.util.Log;

import com.adadapted.android.sdk.core.session.SessionAdapter;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by chrisweeden on 3/23/15.
 */
public class HttpSessionAdapter implements SessionAdapter {
    private static final String TAG = HttpSessionAdapter.class.getName();

    private final Set<Listener> listeners;

    private final String initUrl;
    private final String reinitUrl;

    public HttpSessionAdapter(String initUrl, String reinitUrl) {
        this.listeners = new HashSet<>();

        this.initUrl = initUrl;
        this.reinitUrl = reinitUrl;
    }

    public void sendInit(JSONObject json) {
        Log.i(TAG, "session/init JSON: " + json);
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST,
                initUrl, json, new Response.Listener<JSONObject>(){

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, "Session Init Request Succeeded.");
                notifySessionRequestCompleted(response);
            }

        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Session Init Request Failed.", error);
            }

        });

        HttpRequestManager.getQueue().add(jsonRequest);
    }

    @Override
    public void sendReinit(JSONObject json) {
        Log.i(TAG, "session/reinit JSON: " + json);
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST,
                reinitUrl, json, new Response.Listener<JSONObject>(){

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, "Session Reinit Request Succeeded.");
                notifySessionRequestCompleted(response);
            }

        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Session Reinit Request Failed.", error);
            }

        });

        HttpRequestManager.getQueue().add(jsonRequest);
    }

    public void addListener(Listener listener) {
        this.listeners.add(listener);
    }

    public void removeListener(Listener listener) {
        this.listeners.remove(listener);
    }

    public void notifySessionRequestCompleted(JSONObject response) {
        for(Listener listener : listeners) {
            listener.onSessionRequestCompleted(response);
        }
    }

    @Override
    public String toString() {
        return "HttpSessionAdapter{" +
                "initUrl='" + initUrl + '\'' +
                ", reinitUrl='" + reinitUrl + '\'' +
                '}';
    }
}
