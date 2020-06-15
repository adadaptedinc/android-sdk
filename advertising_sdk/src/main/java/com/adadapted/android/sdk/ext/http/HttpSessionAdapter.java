package com.adadapted.android.sdk.ext.http;

import android.util.Log;

import com.adadapted.android.sdk.config.EventStrings;
import com.adadapted.android.sdk.core.device.DeviceInfo;
import com.adadapted.android.sdk.core.event.AppEventClient;
import com.adadapted.android.sdk.core.session.SessionAdapter;
import com.adadapted.android.sdk.core.session.Session;
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
    @SuppressWarnings("unused")
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
                if(error != null && error.networkResponse != null) {
                    final int statusCode = error.networkResponse.statusCode;

                    if(statusCode >= 400) {
                        final String data = new String(error.networkResponse.data);

                        final Map<String, String> params = new HashMap<>();
                        params.put("url", initUrl);
                        params.put("status_code", Integer.toString(statusCode));
                        params.put("data", data);

                        try {
                            AppEventClient.Companion.getInstance().trackError(
                                    EventStrings.SESSION_REQUEST_FAILED,
                                    error.getMessage(),
                                    params
                            );
                        }
                        catch(IllegalArgumentException illegalArg) {
                            Log.e(LOGTAG, "AppEventClient was not initialized, is your API key valid? -DETAIL: " + illegalArg.getMessage());
                        }
                    }
                }

                listener.onSessionInitializeFailed();
            }

        });

        HttpRequestManager.queueRequest(jsonRequest);
    }

    @Override
    public void sendRefreshAds(final Session session,
                               final AdGetListener listener) {
        if(session == null || listener == null || sessionBuilder == null) {
            return;
        }

        final String url = refreshUrl
                .concat(String.format("?aid=%s", session.getDeviceInfo().getAppId()))
                .concat(String.format("&uid=%s", session.getDeviceInfo().getUdid()))
                .concat(String.format("&sid=%s", session.getId()))
                .concat(String.format("&sdk=%s", session.getDeviceInfo().getSdkVersion()));

        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    final Session session = sessionBuilder.buildSession(response);
                    listener.onNewAdsLoaded(session);
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if(error != null && error.networkResponse != null) {
                        final int statusCode = error.networkResponse.statusCode;
                        if(statusCode >= 400) {
                            final String data = new String(error.networkResponse.data);
                            final Map<String, String> params = new HashMap<>();
                            params.put("url", refreshUrl);
                            params.put("status_code", Integer.toString(statusCode));
                            params.put("data", data);
                            AppEventClient.Companion.getInstance().trackError(
                                    EventStrings.AD_GET_REQUEST_FAILED,
                                    error.getMessage(),
                                    params
                            );
                        }
                    }
                    listener.onNewAdsLoadFailed();
                }
            }
        );

        HttpRequestManager.queueRequest(request);
    }
}
