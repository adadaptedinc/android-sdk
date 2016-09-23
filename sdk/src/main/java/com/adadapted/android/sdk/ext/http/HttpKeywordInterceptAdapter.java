package com.adadapted.android.sdk.ext.http;

import android.util.Log;

import com.adadapted.android.sdk.core.keywordintercept.KeywordInterceptAdapter;
import com.adadapted.android.sdk.core.keywordintercept.KeywordInterceptInitListener;
import com.adadapted.android.sdk.core.keywordintercept.KeywordInterceptTrackListener;
import com.adadapted.android.sdk.ext.factory.AnomalyTrackerFactory;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by chrisweeden on 6/23/15.
 */
public class HttpKeywordInterceptAdapter implements KeywordInterceptAdapter {
    private static final String LOGTAG = HttpKeywordInterceptAdapter.class.getName();

    private final String mInitUrl;
    private final String mTrackUrl;

    public HttpKeywordInterceptAdapter(final String initUrl,
                                       final String trackUrl) {
        mInitUrl = initUrl == null ? "" : initUrl;
        mTrackUrl = trackUrl == null ? "" : trackUrl;
    }

    @Override
    public void init(final JSONObject json,
                     final KeywordInterceptInitListener listener) {
        if(json == null || listener == null) {
            return;
        }

        final JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST,
                mInitUrl, json, new Response.Listener<JSONObject>(){
            @Override
            public void onResponse(JSONObject jsonObject) {
                listener.onSuccess(jsonObject);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(LOGTAG, "KI Init Request Failed.");
                AnomalyTrackerFactory.registerAnomaly("",
                        mInitUrl,
                        "KI_SESSION_REQUEST_FAILED",
                        error.getMessage());
                listener.onFailure();
            }
        });

        HttpRequestManager.getQueue().add(jsonRequest);

        listener.onSuccess(new JSONObject());
    }

    @Override
    public void track(final JSONArray json,
                      final KeywordInterceptTrackListener listener) {
        if(json == null || listener == null) {
            return;
        }

        final JsonArrayRequest jsonRequest = new JsonArrayRequest(Request.Method.POST,
                mTrackUrl, json, new Response.Listener<JSONArray>(){
            @Override
            public void onResponse(JSONArray jsonObject) {
                listener.onSuccess();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(LOGTAG, "KI Track Request Failed.");
                AnomalyTrackerFactory.registerAnomaly("",
                        mTrackUrl,
                        "KI_EVENT_REQUEST_FAILED",
                        error.getMessage());
                listener.onFailure(json);
            }
        });

        HttpRequestManager.getQueue().add(jsonRequest);
    }
}
