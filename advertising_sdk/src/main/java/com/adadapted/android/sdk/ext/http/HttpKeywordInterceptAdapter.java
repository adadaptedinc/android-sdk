package com.adadapted.android.sdk.ext.http;

import android.util.Log;

import com.adadapted.android.sdk.core.keywordintercept.KeywordInterceptAdapter;
import com.adadapted.android.sdk.core.keywordintercept.KeywordInterceptBuilder;
import com.adadapted.android.sdk.core.keywordintercept.model.KeywordIntercept;
import com.adadapted.android.sdk.ext.management.AdAnomalyTrackingManager;
import com.adadapted.android.sdk.ext.management.AppErrorTrackingManager;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by chrisweeden on 6/23/15.
 */
public class HttpKeywordInterceptAdapter implements KeywordInterceptAdapter {
    private static final String LOGTAG = HttpKeywordInterceptAdapter.class.getName();

    private final String endpoint;
    private final KeywordInterceptBuilder builder;

    public HttpKeywordInterceptAdapter(final String endpoint,
                                       final KeywordInterceptBuilder builder) {
        this.endpoint = endpoint == null ? "" : endpoint;
        this.builder = builder;
    }

    @Override
    public void init(final JSONObject json,
                     final KeywordInterceptAdapter.Callback callback) {
        if(json == null || callback == null) {
            return;
        }

        final JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST,
                endpoint, json, new Response.Listener<JSONObject>(){
            @Override
            public void onResponse(JSONObject jsonObject) {
                final KeywordIntercept ki = builder.build(jsonObject);
                callback.onSuccess(ki);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(LOGTAG, "KI Init Request Failed.");

                final Map<String, String> params = new HashMap<>();
                params.put("url", endpoint);
                AppErrorTrackingManager.registerEvent(
                        "KI_SESSION_REQUEST_FAILED",
                        error.getMessage(),
                        params);

                callback.onFailure();
            }
        });

        HttpRequestManager.queueRequest(jsonRequest);
    }
}
