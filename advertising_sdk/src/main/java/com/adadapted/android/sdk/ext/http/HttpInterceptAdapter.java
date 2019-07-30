package com.adadapted.android.sdk.ext.http;

import android.util.Log;

import com.adadapted.android.sdk.core.event.AppEventClient;
import com.adadapted.android.sdk.core.intercept.InterceptAdapter;
import com.adadapted.android.sdk.core.intercept.InterceptEvent;
import com.adadapted.android.sdk.core.session.Session;
import com.adadapted.android.sdk.ext.json.JsonInterceptBuilder;
import com.adadapted.android.sdk.ext.json.JsonInterceptEventBuilder;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class HttpInterceptAdapter implements InterceptAdapter {
    @SuppressWarnings("unused")
    private static final String LOGTAG = HttpInterceptAdapter.class.getName();

    private final String initUrl;
    private final JsonInterceptBuilder kiBuilder;

    private final String eventUrl;
    private final JsonInterceptEventBuilder eventBuilder;

    public HttpInterceptAdapter(final String initUrl,
                                final String eventUrl) {
        this.initUrl = initUrl == null ? "" : initUrl;
        this.kiBuilder = new JsonInterceptBuilder();

        this.eventUrl = eventUrl;
        this.eventBuilder = new JsonInterceptEventBuilder();
    }

    @Override
    public void retrieve(final Session session, final Callback callback) {
        if(session == null || session.getId().isEmpty()) {
            return;
        }

        final String url = initUrl
                .concat(String.format("?aid=%s", session.getDeviceInfo().getAppId()))
                .concat(String.format("&uid=%s", session.getDeviceInfo().getUdid()))
                .concat(String.format("&sid=%s", session.getId()))
                .concat(String.format("&sdk=%s", session.getDeviceInfo().getSdkVersion()));

        final JsonObjectRequest jsonRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>(){
            @Override
            public void onResponse(JSONObject response) {
                Log.i(LOGTAG, response.toString());
                callback.onSuccess(kiBuilder.build(response));
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
    public void sendEvents(final Session session, final Set<InterceptEvent> events) {
        final JSONObject json = eventBuilder.marshalEvents(session, events);
        Log.i(LOGTAG, json.toString());

        final JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST,
                eventUrl, json, new Response.Listener<JSONObject>(){
            @Override
            public void onResponse(JSONObject response) {}
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
