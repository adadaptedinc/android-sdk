package com.adadapted.android.sdk.ext.json

import android.util.Log
import com.adadapted.android.sdk.config.EventStrings
import com.adadapted.android.sdk.core.common.DimensionConverter
import com.adadapted.android.sdk.core.device.DeviceInfo
import com.adadapted.android.sdk.core.event.AppEventClient.Companion.getInstance
import com.adadapted.android.sdk.core.session.Session
import com.google.gson.GsonBuilder
import org.json.JSONException
import org.json.JSONObject

class JsonSessionBuilder(private val deviceInfo: DeviceInfo) {

    fun buildSession(response: JSONObject): Session {
        try {

            DimensionConverter.createInstance(deviceInfo.scale)
            val gson = GsonBuilder().create()
            val session =  gson.fromJson(response.toString(), Session::class.java)
            session.setDeviceInfo(deviceInfo)

//            val session = Session(
//                    deviceInfo,
//                    response.getString(SESSION_ID),
//                    response.getBoolean(WILL_SERVE_ADS),
//                    response.getBoolean(ACTIVE_CAMPAIGNS),
//                    response.getLong(POLLING_INTERVAL_MS),
//                    response.getLong(SESSION_EXPIRES_AT))
                    //Session.convertExpirationToDate(response.getLong(SESSION_EXPIRES_AT)))

//            if (session.hasActiveCampaigns()) {
//                if (response.has(ZONES) && response[ZONES].javaClass == JSONObject::class.java) {
//                    val jsonZones = response.getJSONObject(ZONES)
//                    val zones = zoneBuilder.buildZones(jsonZones)
//                    session.setZones(zones)
//                } else {
//                    Log.i(LOGTAG, "No ads returned. Not parsing JSONArray.")
//                }
//            }
            return session

        } catch (ex: JSONException) {
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
        private const val SESSION_ID = "session_id"
        private const val WILL_SERVE_ADS = "will_serve_ads"
        private const val ACTIVE_CAMPAIGNS = "active_campaigns"
        private const val SESSION_EXPIRES_AT = "session_expires_at"
        private const val POLLING_INTERVAL_MS = "polling_interval_ms"
        private const val ZONES = "zones"
    }
}