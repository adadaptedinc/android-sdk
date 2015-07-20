package com.adadapted.android.sdk.ext.http;

import android.util.Log;

import com.adadapted.android.sdk.core.ad.AdAdapter;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by chrisweeden on 4/1/15.
 */
public class HttpAdAdapter implements AdAdapter {
    private static final String TAG = HttpAdAdapter.class.getName();

    private final Set<Listener> listeners;
    private final String adGetUrl;

    public HttpAdAdapter(String adGetUrl) {
        this.listeners = new HashSet<>();
        this.adGetUrl = adGetUrl;
    }

    @Override
    public void getAds(JSONObject json) {
        JsonObjectRequest request = new JsonObjectRequest(adGetUrl, json,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    notifyAdGetRequestCompleted(jsonObject);
                    Log.i(TAG, "Ad Get Request Succeeded.");
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    notifyAdGetRequestFailed();
                    Log.i(TAG, "Ad Get Request Failed.");
                }
            }
        );

        HttpRequestManager.getQueue().add(request);
    }

    @Override
    public void addListener(Listener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(Listener listener) {
        listeners.remove(listener);
    }

    @Override
    public void notifyAdGetRequestCompleted(JSONObject adJson) {
        for(Listener listener : listeners) {
            listener.onAdGetRequestCompleted(adJson);
        }
    }

    @Override
    public void notifyAdGetRequestFailed() {
        for(Listener listener : listeners) {
            listener.onAdGetRequestFailed();
        }
    }
}
