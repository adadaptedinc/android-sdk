package com.adadapted.android.sdk.ext.http;

import android.util.Log;

import com.adadapted.android.sdk.config.EventStrings;
import com.adadapted.android.sdk.core.device.DeviceInfo;
import com.adadapted.android.sdk.core.event.AppError;
import com.adadapted.android.sdk.core.event.AppEvent;
import com.adadapted.android.sdk.core.event.AppEventClient;
import com.adadapted.android.sdk.core.event.AppEventSink;
import com.adadapted.android.sdk.ext.json.JsonAppErrorBuilder;
import com.adadapted.android.sdk.ext.json.JsonAppEventBuilder;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class HttpAppEventSink implements AppEventSink {
    private static final String LOGTAG = HttpAppEventSink.class.getName();

    private final String eventUrl;
    private final String errorUrl;

    private final JsonAppEventBuilder eventBuilder;
    private final JsonAppErrorBuilder errorBuilder;

    private JSONObject eventWrapper;
    private JSONObject errorWrapper;

    public HttpAppEventSink(final String eventUrl,
                            final String errorUrl) {
        this.eventUrl = eventUrl;
        this.errorUrl = errorUrl;

        eventBuilder = new JsonAppEventBuilder();
        errorBuilder = new JsonAppErrorBuilder();
    }

    @Override
    public void generateWrappers(final DeviceInfo deviceInfo) {
        eventWrapper = eventBuilder.buildWrapper(deviceInfo);
        errorWrapper = errorBuilder.buildWrapper(deviceInfo);
    }

    @Override
    public void publishEvent(final Set<AppEvent> events) {
        if(eventWrapper == null) {
            Log.w(LOGTAG, "No event wrapper");
            return;
        }

        final JSONObject json = eventBuilder.buildItem(eventWrapper, events);

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
                        AppEventClient.Companion.getInstance().trackError(
                                EventStrings.APP_EVENT_REQUEST_FAILED,
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
    public void publishError(final Set<AppError> errors) {
        if(errorWrapper == null) {
            Log.w(LOGTAG, "No error wrapper");
            return;
        }

        final JSONObject json = errorBuilder.buildItem(errorWrapper, errors);

        final JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST,
                errorUrl, json, new Response.Listener<JSONObject>(){
            @Override
            public void onResponse(JSONObject response) {}

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error != null && error.networkResponse != null) {
                    final int statusCode = error.networkResponse.statusCode;
                    final String data = new String(error.networkResponse.data);

                    Log.e(LOGTAG, "App Error Request Failed: " + statusCode + " - " + data, error);
                }
            }
        });

        HttpRequestManager.queueRequest(jsonRequest);
    }
}
