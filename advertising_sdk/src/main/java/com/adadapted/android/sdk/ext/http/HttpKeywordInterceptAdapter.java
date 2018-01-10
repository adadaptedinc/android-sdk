package com.adadapted.android.sdk.ext.http;

import com.adadapted.android.sdk.core.event.AppEventClient;
import com.adadapted.android.sdk.core.keywordintercept.KeywordIntercept;
import com.adadapted.android.sdk.core.keywordintercept.KeywordInterceptAdapter;
import com.adadapted.android.sdk.core.keywordintercept.KeywordInterceptEvent;
import com.adadapted.android.sdk.core.session.Session;
import com.adadapted.android.sdk.ext.json.JsonKeywordInterceptBuilder;
import com.adadapted.android.sdk.ext.json.JsonKeywordInterceptEventBuilder;
import com.adadapted.android.sdk.ext.json.JsonKeywordInterceptRequestBuilder;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class HttpKeywordInterceptAdapter implements KeywordInterceptAdapter {
    @SuppressWarnings("unused")
    private static final String LOGTAG = HttpKeywordInterceptAdapter.class.getName();

    private final String initUrl;
    private final JsonKeywordInterceptRequestBuilder requestBuilder;
    private final JsonKeywordInterceptBuilder kiBuilder;

    private final String eventUrl;
    private final JsonKeywordInterceptEventBuilder eventBuilder;

    public HttpKeywordInterceptAdapter(final String initUrl,
                                       final String eventUrl) {
        this.initUrl = initUrl == null ? "" : initUrl;

        this.requestBuilder = new JsonKeywordInterceptRequestBuilder();
        this.kiBuilder = new JsonKeywordInterceptBuilder();

        this.eventUrl = eventUrl;
        this.eventBuilder = new JsonKeywordInterceptEventBuilder();
    }

    @Override
    public void init(final Session session, final Callback callback) {
        final JSONObject json = requestBuilder.buildInitRequest(session);

        final JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, initUrl, json, new Response.Listener<JSONObject>(){
            @Override
            public void onResponse(JSONObject response) {
                final KeywordIntercept ki = kiBuilder.build(response);
                callback.onSuccess(ki);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error != null && error.networkResponse != null) {
                    final int statusCode = error.networkResponse.statusCode;

                    if(statusCode >= 400) {
                        final String data = new String(error.networkResponse.data);

                        final Map<String, String> params = new HashMap<>();
                        params.put("url", initUrl);
                        params.put("status_code", Integer.toString(statusCode));
                        params.put("data", data);
                        AppEventClient.trackError(
                                "KI_SESSION_REQUEST_FAILED",
                                error.getMessage(),
                                params
                        );
                    }
                }
            }
        });

        HttpRequestManager.queueRequest(jsonRequest);
    }

    @Override
    public void sendBatch(final Set<KeywordInterceptEvent> events) {
        final JSONArray json = eventBuilder.buildEvents(events);

        final JsonArrayRequest jsonRequest = new JsonArrayRequest(Request.Method.POST,
                eventUrl, json, new Response.Listener<JSONArray>(){
            @Override
            public void onResponse(JSONArray response) {
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error != null && error.networkResponse != null) {
                    final int statusCode = error.networkResponse.statusCode;

                    if(statusCode >= 400) {
                        final String data = new String(error.networkResponse.data);

                        final Map<String, String> params = new HashMap<>();
                        params.put("url", eventUrl);
                        params.put("status_code", Integer.toString(statusCode));
                        params.put("data", data);
                        AppEventClient.trackError(
                                "KI_EVENT_REQUEST_FAILED",
                                error.getMessage(),
                                params
                        );
                    }
                }
            }
        });

        HttpRequestManager.queueRequest(jsonRequest);
    }
}
