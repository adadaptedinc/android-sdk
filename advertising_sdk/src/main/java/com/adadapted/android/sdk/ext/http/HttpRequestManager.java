package com.adadapted.android.sdk.ext.http;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class HttpRequestManager {
    private static final String LOGTAG = HttpRequestManager.class.getName();

    private static RequestQueue requestQueue;

    private HttpRequestManager() {}

    public static synchronized void createQueue(final Context context) {
        if(requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
    }

    static synchronized void queueRequest(final Request request) {
        if(requestQueue != null) {
            requestQueue.add(request);
        }
        else {
            Log.e(LOGTAG, "HTTP Request Queue has not been initialized.");
        }
    }
}
