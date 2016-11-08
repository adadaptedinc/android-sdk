package com.adadapted.android.sdk.ext.http;

import android.util.Log;

import com.adadapted.android.sdk.core.anomaly.AnomalyAdapter;
import com.adadapted.android.sdk.ext.management.AppErrorTrackingManager;
import com.android.volley.Request;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by chrisweeden on 9/23/16.
 */
public class HttpAnomalyAdapter implements AnomalyAdapter {
    private static final String LOGTAG = HttpAnomalyAdapter.class.getName();

    private final String mBatchUrl;

    public HttpAnomalyAdapter(String batchUrl) {
        this.mBatchUrl = batchUrl;
    }

    @Override
    public void sendBatch(final JSONArray json) {
        if(json == null) {
            return;
        }
        final StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                mBatchUrl,
                new Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                    }
                }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(LOGTAG, "Anomaly Track Request Failed.", error);

                final Map<String, String> params = new HashMap<>();
                params.put("url", mBatchUrl);
                AppErrorTrackingManager.registerEvent(
                        "ANOMALY_TRACK_REQUEST_FAILED",
                        error.getMessage(),
                        params);
            }
        }){
            @Override
            public byte[] getBody() {
                return json.toString().getBytes();
            }
        };

        HttpRequestManager.queueRequest(stringRequest);
    }
}
