package com.adadapted.sdk.addit.ext.http;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;


/**
 * Created by chrisweeden on 3/24/15.
 */
public class HttpRequestManager {
    private static final String LOGTAG = HttpRequestManager.class.getName();

    private static RequestQueue requestQueue;

    private HttpRequestManager() {}

    public static synchronized void createQueue(final Context context) {
        if(requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
    }

    public static synchronized RequestQueue getQueue() {
        return requestQueue;
    }
}
