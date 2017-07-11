package com.adadapted.android.sdk.ext.http;

import android.util.Log;

import com.adadapted.android.sdk.core.device.DeviceInfo;
import com.adadapted.android.sdk.core.event.AppEventClient;
import com.adadapted.android.sdk.core.session.SessionAdapter;
import com.adadapted.android.sdk.core.session.Session;
import com.adadapted.android.sdk.core.zone.Zone;
import com.adadapted.android.sdk.ext.json.JsonAdRefreshBuilder;
import com.adadapted.android.sdk.ext.json.JsonAdRequestBuilder;
import com.adadapted.android.sdk.ext.json.JsonSessionBuilder;
import com.adadapted.android.sdk.ext.json.JsonSessionRequestBuilder;
import com.adadapted.android.sdk.ext.json.JsonZoneBuilder;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
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
        final JsonSessionBuilder sessionBuilder = new JsonSessionBuilder(deviceInfo);

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
                Log.w(LOGTAG, "Session Init Request Failed.", error);

                if(error instanceof NoConnectionError || error instanceof NetworkError) {
                    return;
                }

                final Map<String, String> params = new HashMap<>();
                params.put("url", initUrl);
                AppEventClient.trackError(
                        "SESSION_REQUEST_FAILED",
                        error.getMessage(),
                        params
                );

                listener.onSessionInitializeFailed();
            }

        });

        HttpRequestManager.queueRequest(jsonRequest);
    }

    @Override
    public void sentAdGet(final Session session, final AdGetListener listener) {
        if(session == null || listener == null) {
            return;
        }

        final JsonAdRequestBuilder requestBuilder = new JsonAdRequestBuilder();
        final JsonAdRefreshBuilder builder = new JsonAdRefreshBuilder(new JsonZoneBuilder(session.getDeviceInfo().getScale()));

        final JSONObject json = requestBuilder.buildAdRequest(session);

        final JsonObjectRequest jsonRequest = new JsonObjectRequest(refreshUrl, json,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        final Map<String, Zone> zones = builder.buildRefreshedAds(jsonObject);
                        listener.onNewAdsLoaded(zones);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i(LOGTAG, "Ad Get Request Failed.");

                        if(error instanceof NoConnectionError || error instanceof NetworkError) {
                            return;
                        }

                        final Map<String, String> params = new HashMap<>();
                        params.put("url", refreshUrl);
                        AppEventClient.trackError(
                                "AD_GET_REQUEST_FAILED",
                                error.getMessage(),
                                params
                        );

                        listener.onNewAdsLoadFailed();
                    }
                }
        );

        HttpRequestManager.queueRequest(jsonRequest);
    }
}
