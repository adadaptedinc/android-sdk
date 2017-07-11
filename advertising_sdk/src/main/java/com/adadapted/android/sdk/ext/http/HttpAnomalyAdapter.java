package com.adadapted.android.sdk.ext.http;

import android.util.Log;

import com.adadapted.android.sdk.core.ad.AdAnomalySink;
import com.adadapted.android.sdk.core.event.AppEventClient;
import com.adadapted.android.sdk.core.session.Session;
import com.adadapted.android.sdk.ext.json.JsonAnomalyBuilder;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class HttpAnomalyAdapter implements AdAnomalySink {
    private static final String LOGTAG = HttpAnomalyAdapter.class.getName();

    private final String mBatchUrl;

    public HttpAnomalyAdapter(String batchUrl) {
        this.mBatchUrl = batchUrl;
    }

    @Override
    public void sendBatch(final Session session,
                          final String adId,
                          final String eventPath,
                          final String code,
                          final String message) {
        final JsonAnomalyBuilder builder = new JsonAnomalyBuilder();
        final JSONObject json = builder.build(session, adId, eventPath, code, message);

        final StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                mBatchUrl,
                new Listener<String>() {
                    @Override
                    public void onResponse(final String s) {}
                }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(LOGTAG, "Anomaly Track Request Failed.", error);

                if(error instanceof NoConnectionError || error instanceof NetworkError) {
                    return;
                }

                final Map<String, String> params = new HashMap<>();
                params.put("url", mBatchUrl);
                AppEventClient.trackError(
                    "ANOMALY_TRACK_REQUEST_FAILED",
                    error.getMessage(),
                    params
                );
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
