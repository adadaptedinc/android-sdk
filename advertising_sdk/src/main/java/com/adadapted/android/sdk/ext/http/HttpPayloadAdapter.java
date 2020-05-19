package com.adadapted.android.sdk.ext.http;

import android.util.Log;

import com.adadapted.android.sdk.core.addit.AdditContent;
import com.adadapted.android.sdk.core.addit.PayloadAdapter;
import com.adadapted.android.sdk.core.addit.PayloadContentParser;
import com.adadapted.android.sdk.core.addit.PayloadEvent;
import com.adadapted.android.sdk.core.device.DeviceInfo;
import com.adadapted.android.sdk.core.event.AppEventClient;
import com.adadapted.android.sdk.ext.json.JsonPayloadBuilder;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpPayloadAdapter implements PayloadAdapter {
    private static final String LOGTAG = HttpPayloadAdapter.class.getName();

    private final String pickupUrl;
    private final String trackUrl;

    private final JsonPayloadBuilder builder;
    private final PayloadContentParser parser;

    public HttpPayloadAdapter(final String pickupUrl,
                              final String trackUrl) {
        this.pickupUrl = pickupUrl;
        this.trackUrl = trackUrl;

        this.builder = new JsonPayloadBuilder();
        this.parser = new PayloadContentParser();
    }

    @Override
    public void pickup(final DeviceInfo deviceInfo,
                       final Callback callback) {
        if(deviceInfo == null || callback == null) {
            return;
        }

        final JSONObject json = builder.buildRequest(deviceInfo);

        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, pickupUrl, json, new Response.Listener<JSONObject>(){
            @Override
            public void onResponse(JSONObject response) {
                final List<AdditContent> content = parser.parse(response);
                callback.onSuccess(content);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error != null && error.networkResponse != null) {
                    final int statusCode = error.networkResponse.statusCode;

                    if(statusCode >= 400) {
                        final String data = new String(error.networkResponse.data);

                        final Map<String, String> params = new HashMap<>();
                        params.put("url", pickupUrl);
                        params.put("status_code", Integer.toString(statusCode));
                        params.put("data", data);
                        AppEventClient.Companion.getInstance().trackError(
                                "PAYLOAD_PICKUP_REQUEST_FAILED",
                                error.getMessage(),
                                params
                        );
                    }
                }
            }
        });

        HttpRequestManager.queueRequest(request);
    }

    @Override
    public void publishEvent(final PayloadEvent event) {
        final JSONObject json = builder.buildEvent(event);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, trackUrl, json, new Response.Listener<JSONObject>() {
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
                        params.put("url", trackUrl);
                        params.put("status_code", Integer.toString(statusCode));
                        params.put("data", data);
                        AppEventClient.Companion.getInstance().trackError(
                                "PAYLOAD_EVENT_REQUEST_FAILED",
                                error.getMessage(),
                                params
                        );
                    }
                }
            }
        });

        HttpRequestManager.queueRequest(request);
    }
}
