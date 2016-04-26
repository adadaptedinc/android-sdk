package com.adadapted.android.sdk.ext.http;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by chrisweeden on 3/24/15.
 */
class HttpRequestManager {
    private static final String LOGTAG = HttpRequestManager.class.getName();

    private static RequestQueue requestQueue;

    private HttpRequestManager() {}

    public static synchronized void createQueue(Context context) {
        if(requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context);
        }
    }

    public static synchronized RequestQueue getQueue() {
        return requestQueue;
    }
}
