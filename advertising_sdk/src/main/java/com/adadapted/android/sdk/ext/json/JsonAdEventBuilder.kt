package com.adadapted.android.sdk.ext.json

import android.util.Log
import com.adadapted.android.sdk.core.ad.AdEvent
import com.adadapted.android.sdk.core.session.Session
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class JsonAdEventBuilder {
    fun marshalEvents(session: Session, events: Set<AdEvent>): JSONObject {
        val wrapper = JSONObject()
        try {
            wrapper.put(SESSION_ID, session.id)
            wrapper.put(APP_ID, session.getDeviceInfo().appId)
            wrapper.put(UDID, session.getDeviceInfo().udid)
            wrapper.put(SDK_VERSION, session.getDeviceInfo().sdkVersion)
            wrapper.put(EVENTS, buildEvents(events))
        } catch (ex: JSONException) {
            Log.w(LOGTAG, "Problem building Intercept Event JSON")
        }
        return wrapper
    }

    @Throws(JSONException::class)
    private fun buildEvents(events: Set<AdEvent>): JSONArray {
        val jsonArray = JSONArray()
        for (event in events) {
            val json = JSONObject()
            json.put(AD_ID, event.adId)
            json.put(IMPRESSION_ID, event.impressionId)
            json.put(EVENT_TYPE, event.eventType)
            json.put(CREATED_AT, event.createdAt)
            jsonArray.put(json)
        }
        return jsonArray
    }

    companion object {
        private val LOGTAG = JsonAdEventBuilder::class.java.name
        private const val APP_ID = "app_id"
        private const val SESSION_ID = "session_id"
        private const val UDID = "udid"
        private const val EVENTS = "events"
        private const val SDK_VERSION = "sdk_version"
        private const val AD_ID = "ad_id"
        private const val IMPRESSION_ID = "impression_id"
        private const val EVENT_TYPE = "event_type"
        private const val CREATED_AT = "created_at"
    }
}