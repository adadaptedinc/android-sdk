package com.adadapted.android.sdk.ext.http;

import android.util.Log;

import com.adadapted.android.sdk.core.keywordintercept.KeywordInterceptAdapter;
import com.adadapted.android.sdk.core.keywordintercept.model.KeywordIntercept;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by chrisweeden on 6/23/15.
 */
public class HttpKeywordInterceptAdapter implements KeywordInterceptAdapter {
    private static final String TAG = HttpKeywordInterceptAdapter.class.getName();

    private final String initUrl;
    private final String trackUrl;

    private final Set<Listener> listeners;

    public HttpKeywordInterceptAdapter(String initUrl, String trackUrl) {
        this.listeners = new HashSet<>();

        this.initUrl = initUrl;
        this.trackUrl = trackUrl;
    }

    @Override
    public void init(JSONObject json) {
        Log.i(TAG, "ki/init JSON: " + json);
        //JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST,
        //        initUrl, json, new Response.Listener<JSONObject>(){
        //    @Override
        //    public void onResponse(JSONObject jsonObject) {
        //        Log.d(TAG, "KI Init Request Succeeded.");
        //        notifyInitSuccess(jsonObject);
        //    }
        //}, new Response.ErrorListener() {
        //    @Override
        //    public void onErrorResponse(VolleyError volleyError) {
        //        Log.d(TAG, "KI Init Request Failed.");
        //        notifyInitFailed();
        //    }
        //});

        //HttpRequestManager.getQueue().add(jsonRequest);

        notifyInitSuccess(new JSONObject());
    }

    @Override
    public void track(JSONArray json) {
        Log.i(TAG, "ki/track JSON: " + json);
        //JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST,
        //        trackUrl, json, new Response.Listener<JSONObject>(){
        //    @Override
        //    public void onResponse(JSONObject jsonObject) {
        //        Log.d(TAG, "KI Track Request Succeeded.");
        //    }
        //}, new Response.ErrorListener() {
        //    @Override
        //    public void onErrorResponse(VolleyError volleyError) {
        //        Log.d(TAG, "KI Track Request Failed.");
        //    }
        //});

        //HttpRequestManager.getQueue().add(jsonRequest);
    }

    public void addListener(Listener listener) {
        listeners.add(listener);
    }

    public void removeListener(Listener listener) {
        listeners.remove(listener);
    }

    private void notifyInitSuccess(JSONObject jsonObject) {
        for(Listener listener : listeners) {
            listener.onInitSuccess(jsonObject);
        }
    }

    private void notifyInitFailed() {
        for(Listener listener : listeners) {
            listener.onInitFailed();
        }
    }

    private void notifyTrackSuccess() {
        for(Listener listener : listeners) {
            listener.onTrackSuccess();
        }
    }

    private void notifyTrackFailed() {
        for(Listener listener : listeners) {
            listener.onTrackFailed();
        }
    }
}
