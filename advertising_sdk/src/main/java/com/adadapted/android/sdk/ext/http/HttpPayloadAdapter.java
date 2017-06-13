package com.adadapted.android.sdk.ext.http;

import android.util.Log;

import com.adadapted.android.sdk.core.addit.Content;
import com.adadapted.android.sdk.core.addit.payload.PayloadAdapter;
import com.adadapted.android.sdk.core.addit.payload.PayloadContentParser;
import com.adadapted.android.sdk.ext.management.AppErrorTrackingManager;
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

/**
 * Created by chrisweeden on 2/9/17.
 */
public class HttpPayloadAdapter implements PayloadAdapter {
    private static final String LOGTAG = HttpPayloadAdapter.class.getName();

    private final String endpoint;
    private final PayloadContentParser parser;

    public HttpPayloadAdapter(final String endpoint) {
        this.endpoint = endpoint;
        this.parser = new PayloadContentParser();
    }

    @Override
    public void pickup(final JSONObject json, final Callback callback) {
        if(json == null) {
            return;
        }

        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                this.endpoint, json, new Response.Listener<JSONObject>(){
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
                errorParams.put("endpoint", endpoint);
                AppErrorTrackingManager.registerEvent(
                    "PAYLOAD_PICKUP_REQUEST_FAILED",
                    error.getMessage(),
                    errorParams
                );

                callback.onFailure(error.getMessage());
            }
        });

        HttpRequestManager.queueRequest(request);
    }
}
