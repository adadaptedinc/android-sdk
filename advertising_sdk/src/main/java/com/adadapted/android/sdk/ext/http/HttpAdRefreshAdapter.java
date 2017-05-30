package com.adadapted.android.sdk.ext.http;

import android.util.Log;

import com.adadapted.android.sdk.core.ad.AdRefreshAdapter;
import com.adadapted.android.sdk.core.zone.model.Zone;
import com.adadapted.android.sdk.ext.json.JsonAdRefreshBuilder;
import com.adadapted.android.sdk.ext.management.AppErrorTrackingManager;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by chrisweeden on 4/1/15.
 */
public class HttpAdRefreshAdapter implements AdRefreshAdapter {
    private static final String LOGTAG = HttpAdRefreshAdapter.class.getName();

    private final String endpoint;
    private final JsonAdRefreshBuilder builder;

    public HttpAdRefreshAdapter(final String endpoint,
                                final JsonAdRefreshBuilder builder) {
        this.endpoint = endpoint;
        this.builder = builder;
    }

    @Override
    public void getAds(final JSONObject json,
                       final Callback callback) {
        if(json == null || callback == null) {
            return;
        }

        final JsonObjectRequest jsonRequest = new JsonObjectRequest(endpoint, json,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        final Map<String, Zone> zones = builder.buildRefreshedAds(jsonObject);
                        callback.onSuccess(zones);
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
                        params.put("url", endpoint);
                        AppErrorTrackingManager.registerEvent(
                                "AD_GET_REQUEST_FAILED",
                                error.getMessage(),
                                params);

                        callback.onFailure();
                    }
                }
        );

        HttpRequestManager.queueRequest(jsonRequest);
    }
}
