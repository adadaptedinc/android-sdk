package com.adadapted.android.sdk.ext.http

import android.util.Log
import com.adadapted.android.sdk.core.event.AppEventClient
import com.android.volley.VolleyError

object HttpErrorTracker {

    fun trackHttpError(volleyError: VolleyError?, url: String, eventString: String, logTag: String) {
        if (volleyError?.networkResponse != null) {
            val statusCode = volleyError.networkResponse.statusCode
            if (statusCode >= 400) {
                val data = String(volleyError.networkResponse.data)
                val params: MutableMap<String, String> = HashMap()
                params["url"] = url
                params["status_code"] = statusCode.toString()
                params["data"] = data
                try {
                    AppEventClient.getInstance().trackError(eventString, volleyError.message ?: "", params)
                } catch (illegalArg: IllegalArgumentException) {
                    Log.e(logTag, "AppEventClient was not initialized, is your API key valid? -DETAIL: " + illegalArg.message)
                }
            }
        }
    }
}