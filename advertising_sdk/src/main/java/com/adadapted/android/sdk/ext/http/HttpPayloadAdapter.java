package com.adadapted.android.sdk.ext.http;

import android.util.Log;

import com.adadapted.android.sdk.core.addit.Content;
import com.adadapted.android.sdk.core.addit.PayloadAdapter;
import com.adadapted.android.sdk.core.addit.PayloadContentParser;
import com.adadapted.android.sdk.core.event.AppEventClient;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
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

    private final String pickupEndpoint;
    private final String trackEndpoint;
    private final PayloadContentParser parser;

    public HttpPayloadAdapter(final String pickupEndpoint, final String trackEndpoint) {
        this.pickupEndpoint = pickupEndpoint;
        this.trackEndpoint = trackEndpoint;

        this.parser = new PayloadContentParser();
    }

    @Override
    public void pickup(final JSONObject json, final Callback callback) {
        if(json == null) {
            return;
        }

        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                this.pickupEndpoint, json, new Response.Listener<JSONObject>(){
            @Override
            public void onResponse(JSONObject response) {
                final List<Content> content = parser.parse(response);
                callback.onSuccess(content);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(LOGTAG, "Payload Pickup Request Failed.", error);

                if(error instanceof NoConnectionError || error instanceof NetworkError) {
                    return;
                }

                final Map<String, String> errorParams = new HashMap<>();
                errorParams.put("endpoint", pickupEndpoint);
                AppEventClient.trackError(
                    "PAYLOAD_PICKUP_REQUEST_FAILED",
                    error.getMessage(),
                    errorParams
                );

                callback.onFailure(error.getMessage());
            }
        });

        HttpRequestManager.queueRequest(request);
    }

    @Override
    public void publishEvent(JSONObject payloadEvent) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, trackEndpoint, payloadEvent, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {}

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(LOGTAG, "Payload Event Request Failed.", error);

                if(error instanceof NoConnectionError || error instanceof NetworkError) {
                    return;
                }

                final Map<String, String> errorParams = new HashMap<>();
                errorParams.put("endpoint", trackEndpoint);
                errorParams.put("exception", error.getClass().getName());

                AppEventClient.trackError(
                        "PAYLOAD_EVENT_REQUEST_FAILED",
                        error.getMessage(),
                        errorParams
                );
            }
        });

        request.setRetryPolicy(new DefaultRetryPolicy(1000 * 20, 2, 1.0f));

        HttpRequestManager.queueRequest(request);
    }
}
