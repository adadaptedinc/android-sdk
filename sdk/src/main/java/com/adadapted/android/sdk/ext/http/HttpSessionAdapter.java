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
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST,
                initUrl, json, new Response.Listener<JSONObject>(){

            @Override
            public void onResponse(JSONObject response) {
                notifySessionRequestCompleted(response);
            }

        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, "Session Init Request Failed.", error);
                notifySessionRequestFailed();
            }

        });

        HttpRequestManager.getQueue().add(jsonRequest);
    }

    @Override
    public void sendReinit(JSONObject json) {
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST,
                reinitUrl, json, new Response.Listener<JSONObject>(){

            @Override
            public void onResponse(JSONObject response) {
                if(response.length() > 0) {
                    notifySessionRequestCompleted(response);
                }
                else {
                    notifySessionReinitRequestNoContent();
                }
            }

        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, "Session Reinit Request Failed.", error);
                notifySessionReinitRequestFailed();
            }

        });

        HttpRequestManager.getQueue().add(jsonRequest);
    }

    public void addListener(Listener listener) {
        listeners.add(listener);
    }

    public void removeListener(Listener listener) {
        listeners.remove(listener);
    }

    private void notifySessionRequestCompleted(JSONObject response) {
        for(Listener listener : listeners) {
            listener.onSessionInitRequestCompleted(response);
        }
    }

    private void notifySessionRequestFailed() {
        for(Listener listener : listeners) {
            listener.onSessionInitRequestFailed();
        }
    }

    private void notifySessionReinitRequestNoContent() {
        for(Listener listener : listeners) {
            listener.onSessionReinitRequestNoContent();
        }
    }

    private void notifySessionReinitRequestFailed() {
        for(Listener listener : listeners) {
            listener.onSessionReinitRequestFailed();
        }
    }
}
