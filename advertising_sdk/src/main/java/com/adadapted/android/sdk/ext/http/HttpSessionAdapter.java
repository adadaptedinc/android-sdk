package com.adadapted.android.sdk.ext.http;

import android.util.Log;

import com.adadapted.android.sdk.core.device.DeviceInfo;
import com.adadapted.android.sdk.core.event.AppEventClient;
import com.adadapted.android.sdk.core.session.SessionAdapter;
import com.adadapted.android.sdk.core.session.Session;
import com.adadapted.android.sdk.ext.json.JsonAdRequestBuilder;
import com.adadapted.android.sdk.ext.json.JsonSessionBuilder;
import com.adadapted.android.sdk.ext.json.JsonSessionRequestBuilder;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class HttpSessionAdapter implements SessionAdapter {
    private static final String LOGTAG = HttpSessionAdapter.class.getName();

    private final String initUrl;
    private final String refreshUrl;

    private JsonSessionBuilder sessionBuilder;

    public HttpSessionAdapter(final String initUrl,
                              final String refreshUrl) {
        this.initUrl = initUrl == null ? "" : initUrl;
        this.refreshUrl = refreshUrl == null ? "" : refreshUrl;
    }

    @Override
    public void sendInit(final DeviceInfo deviceInfo,
                         final SessionInitListener listener) {
        if(deviceInfo == null || listener == null) {
            return;
        }

        final JsonSessionRequestBuilder requestBuilder = new JsonSessionRequestBuilder();
        sessionBuilder = new JsonSessionBuilder(deviceInfo);

        final JSONObject json = requestBuilder.buildSessionInitRequest(deviceInfo);

        final JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST,
                initUrl, json, new Response.Listener<JSONObject>(){

            @Override
            public void onResponse(final JSONObject response) {

                final Session session = sessionBuilder.buildSession(response);
                listener.onSessionInitialized(session);
            }

        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                String reason = "";
                if(error != null && error.networkResponse != null) {
                    final int statusCode = error.networkResponse.statusCode;
                    final String data = new String(error.networkResponse.data);

                    reason = statusCode + " - " + data;

                    Log.e(LOGTAG, "Session Init Request Failed: " + reason, error);
                }

                final Map<String, String> params = new HashMap<>();
                params.put("url", initUrl);
                AppEventClient.trackError(
                        "SESSION_REQUEST_FAILED",
                        reason,
                        params
                );

                listener.onSessionInitializeFailed();
            }

        });

        HttpRequestManager.queueRequest(jsonRequest);
    }

    @Override
    public void sentAdGet(final Session session,
                          final AdGetListener listener) {
        if(session == null || listener == null || sessionBuilder == null) {
            return;
        }

        final JsonAdRequestBuilder requestBuilder = new JsonAdRequestBuilder();

        final JSONObject json = requestBuilder.buildAdRequest(session);

        final JsonObjectRequest jsonRequest = new JsonObjectRequest(refreshUrl, json,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(final JSONObject response) {
                        final Session session = sessionBuilder.buildSession(response);
                        listener.onNewAdsLoaded(session);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String reason = "";
                        if(error != null && error.networkResponse != null) {
                            final int statusCode = error.networkResponse.statusCode;
                            final String data = new String(error.networkResponse.data);

                            reason = statusCode + " - " + data;

                            Log.e(LOGTAG, "Ad Get Request Failed: " + reason, error);
                        }

                        final Map<String, String> params = new HashMap<>();
                        params.put("url", refreshUrl);
                        AppEventClient.trackError(
                                "AD_GET_REQUEST_FAILED",
                                reason,
                                params
                        );

                        listener.onNewAdsLoadFailed();
                    }
                }
        );

        HttpRequestManager.queueRequest(jsonRequest);
    }
}
