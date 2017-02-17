package com.adadapted.sdk.addit.ext.http;

import android.util.Log;

import com.adadapted.sdk.addit.core.payload.PayloadAdapter;
import com.adadapted.sdk.addit.core.payload.PayloadContent;
import com.adadapted.sdk.addit.core.payload.PayloadContentParser;
import com.adadapted.sdk.addit.ext.management.AppErrorTrackingManager;
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
                final List<PayloadContent> content = parser.parse(response);
                callback.onSuccess(content);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(LOGTAG, "Payload Pickup Request Failed.", error);

                String errorType = "PAYLOAD_PICKUP_REQUEST_FAILED";
                if(error instanceof NoConnectionError || error instanceof NetworkError) {
                    errorType = "PAYLOAD_PICKUP_NO_NETWORK_CONNECTION";
                }

                final Map<String, String> errorParams = new HashMap<>();
                errorParams.put("endpoint", endpoint);
                AppErrorTrackingManager.registerEvent(
                        errorType,
                        error.getMessage(),
                        errorParams);

                callback.onFailure(error.getMessage());
            }
        });

        HttpRequestManager.getQueue().add(request);
    }
}
