package com.adadapted.android.sdk.ext.http;

import android.content.Context;
import android.util.Log;

import com.adadapted.android.sdk.core.session.SessionAdapter;
import com.adadapted.android.sdk.core.session.SessionAdapterListener;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

/**
 * Created by chrisweeden on 3/23/15.
 */
public class HttpSessionAdapter implements SessionAdapter {
    private static final String LOGTAG = HttpSessionAdapter.class.getName();

    private final String mInitUrl;

    public HttpSessionAdapter(Context context, String initUrl) {
        HttpRequestManager.createQueue(context);

        mInitUrl = initUrl;
    }

    @Override
    public void sendInit(JSONObject json, final SessionAdapterListener listener) {
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST,
                mInitUrl, json, new Response.Listener<JSONObject>(){

            @Override
            public void onResponse(JSONObject response) {
                listener.onSuccess(response);
            }

        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(LOGTAG, "Session Init Request Failed.", error);
                listener.onFailure();
            }

        });

        HttpRequestManager.getQueue().add(jsonRequest);
    }
}
