package com.adadapted.android.sdk.ext.json

import android.util.Log
import com.adadapted.android.sdk.config.EventStrings
import com.adadapted.android.sdk.core.event.AppEventClient.Companion.getInstance
import com.adadapted.android.sdk.core.intercept.Intercept
import com.google.gson.GsonBuilder
import org.json.JSONObject
import java.lang.Exception
import kotlin.collections.HashMap

class JsonInterceptBuilder {
    fun build(json: JSONObject?): Intercept {
        if (json == null) {
            return Intercept()
        }
        try {
            val gson = GsonBuilder().create()
            return gson.fromJson(json.toString(), Intercept::class.java)

        } catch (ex: Exception) {
            Log.w(LOGTAG, "Problem parsing JSON", ex)
            val params: MutableMap<String, String> = HashMap()
            params["error"] = ex.message ?: ""
            params["payload"] = json.toString()
            getInstance().trackError(EventStrings.KI_PAYLOAD_PARSE_FAILED, "Failed to parse KI payload for processing.", params)
        }
        return Intercept()
    }

    companion object {
        private val LOGTAG = JsonInterceptBuilder::class.java.name
    }
}