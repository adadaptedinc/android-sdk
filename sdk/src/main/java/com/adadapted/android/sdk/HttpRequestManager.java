package com.adadapted.android.sdk;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by chrisweeden on 3/24/15.
 */
class HttpRequestManager {
    private static final String TAG = HttpRequestManager.class.getName();

    private static RequestQueue requestQueue;

    private HttpRequestManager() { }

    public static synchronized RequestQueue getQueue() {
        if(requestQueue == null) {
            requestQueue = Volley.newRequestQueue(AdAdapted.getInstance().getContext());
        }

        return requestQueue;
    }
}
