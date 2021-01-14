package com.adadapted.android.sdk.ext.json

import android.util.Log
import com.adadapted.android.sdk.core.intercept.InterceptEvent
import com.adadapted.android.sdk.core.session.Session
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class JsonInterceptEventBuilder {
    fun marshalEvents(session: Session, events: Set<InterceptEvent>): JSONObject {
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
    private fun buildEvents(events: Set<InterceptEvent>): JSONArray {
        val jsonArray = JSONArray()
        for (event in events) {
            val json = JSONObject()
            json.put(SEARCH_ID, event.searchId)
            json.put(CREATED_AT, event.createdAt.time)
            json.put(TERM_ID, event.termId)
            json.put(TERM, event.term)
            json.put(USER_INPUT, event.userInput)
            json.put(EVENT_TYPE, event.event)
            jsonArray.put(json)
        }
        return jsonArray
    }

    companion object {
        private val LOGTAG = JSONException::class.java.name
        private const val SESSION_ID = "session_id"
        private const val APP_ID = "app_id"
        private const val UDID = "udid"
        private const val EVENTS = "events"
        private const val SDK_VERSION = "sdk_version"
        private const val SEARCH_ID = "search_id"
        private const val TERM_ID = "term_id"
        private const val TERM = "term"
        private const val USER_INPUT = "user_input"
        private const val EVENT_TYPE = "event_type"
        private const val CREATED_AT = "created_at"
    }
}