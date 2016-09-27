package com.adadapted.sdk.addit.ext.http;

import android.util.Log;

import com.adadapted.sdk.addit.core.anomaly.AnomalySink;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;

/**
 * Created by chrisweeden on 9/26/16.
 */
public class HttpAnomalySink implements AnomalySink {
    private static final String LOGTAG = HttpAnomalySink.class.getName();

    private final String endpoint;

    public HttpAnomalySink(final String endpoint) {
        this.endpoint = endpoint;
    }

    @Override
    public void publishAnomaly(final JSONArray json) {
        if(json == null) {
            return;
        }
        final StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                endpoint,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(LOGTAG, "Anomaly Track Request Failed.", error);
            }
        }){
            @Override
            public byte[] getBody() {
                return json.toString().getBytes();
            }
        };

        HttpRequestManager.getQueue().add(stringRequest);
    }
}
