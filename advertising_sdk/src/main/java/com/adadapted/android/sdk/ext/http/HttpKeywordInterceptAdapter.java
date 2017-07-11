package com.adadapted.android.sdk.ext.http;

import android.util.Log;

import com.adadapted.android.sdk.core.event.AppEventClient;
import com.adadapted.android.sdk.core.keywordintercept.KeywordIntercept;
import com.adadapted.android.sdk.core.keywordintercept.KeywordInterceptAdapter;
import com.adadapted.android.sdk.core.session.Session;
import com.adadapted.android.sdk.ext.json.JsonKeywordInterceptBuilder;
import com.adadapted.android.sdk.ext.json.JsonKeywordInterceptRequestBuilder;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class HttpKeywordInterceptAdapter implements KeywordInterceptAdapter {
    private static final String LOGTAG = HttpKeywordInterceptAdapter.class.getName();

    private final String endpoint;
    private final JsonKeywordInterceptRequestBuilder requestBuilder;
    private final JsonKeywordInterceptBuilder builder;

    public HttpKeywordInterceptAdapter(final String endpoint) {
        this.endpoint = endpoint == null ? "" : endpoint;

        this.requestBuilder = new JsonKeywordInterceptRequestBuilder();
        this.builder = new JsonKeywordInterceptBuilder();
    }

    @Override
    public void init(final Session session, final Callback callback) {
        final JSONObject json = requestBuilder.buildInitRequest(session);

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

                if(error instanceof NoConnectionError || error instanceof NetworkError) {
                    return;
                }

                final Map<String, String> params = new HashMap<>();
                params.put("url", endpoint);
                AppEventClient.trackError(
                    "KI_SESSION_REQUEST_FAILED",
                    error.getMessage(),
                    params
                );

                callback.onFailure();
            }
        });

        HttpRequestManager.queueRequest(jsonRequest);
    }
}
