package com.adadapted.android.sdk.ext.json

import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONObject

class AdAdaptedJsonObjectRequest(
    private var appId: String?,
    method: Int,
    url: String,
    jsonRequest: JSONObject?,
    listener: Response.Listener<JSONObject>,
    errorListener: Response.ErrorListener,
) : JsonObjectRequest(method, url, jsonRequest, listener, errorListener) {

    override fun getHeaders(): MutableMap<String, String> {
        val params: MutableMap<String, String> = HashMap()
        params["X-API-KEY"] = appId ?: ""
        return params
    }
}