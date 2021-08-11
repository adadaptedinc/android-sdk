package com.adadapted.android.sdk.ext.json

import android.util.Log
import com.adadapted.android.sdk.config.EventStrings
import com.adadapted.android.sdk.core.common.DimensionConverter
import com.adadapted.android.sdk.core.device.DeviceInfo
import com.adadapted.android.sdk.core.event.AppEventClient.Companion.getInstance
import com.adadapted.android.sdk.core.session.Session
import com.google.gson.GsonBuilder
import org.json.JSONObject
import java.lang.Exception

class JsonSessionBuilder(private val deviceInfo: DeviceInfo) {
    fun buildSession(response: JSONObject): Session {
        try {
            DimensionConverter.createInstance(deviceInfo.scale)
            val gson = GsonBuilder().create()
            val session = gson.fromJson(response.toString(), Session::class.java)
            session.setDeviceInfo(deviceInfo)
            return session
        } catch (ex: Exception) {
            Log.w(LOGTAG, "Problem converting to JSON.", ex)
            val params: MutableMap<String, String> = HashMap()
            params["exception"] = ex.message ?: ""
            params["bad_json"] = response.toString()
            getInstance().trackError(
                EventStrings.SESSION_PAYLOAD_PARSE_FAILED,
                "Failed to parse Session payload for processing.",
                params.toMap()
            )
        }
        return Session()
    }

    companion object {
        private val LOGTAG = JsonSessionBuilder::class.java.name
    }
}