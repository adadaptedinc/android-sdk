package com.adadapted.android.sdk.ext.http;

import android.util.Log;

import com.adadapted.android.sdk.core.ad.AdAdapter;
import com.adadapted.android.sdk.core.ad.AdAdapterListener;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

/**
 * Created by chrisweeden on 4/1/15.
 */
public class HttpAdAdapter implements AdAdapter {
    private static final String LOGTAG = HttpAdAdapter.class.getName();

    private final String mAdGetUrl;

    public HttpAdAdapter(final String adGetUrl) {
        mAdGetUrl = adGetUrl;
    }

    @Override
    public void getAds(final JSONObject json, final AdAdapterListener listener) {
        JsonObjectRequest request = new JsonObjectRequest(mAdGetUrl, json,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    listener.onSuccess(jsonObject);
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    listener.onFailure();
                    Log.i(LOGTAG, "Ad Get Request Failed.");
                }
            }
        );

        HttpRequestManager.getQueue().add(request);
    }
}
