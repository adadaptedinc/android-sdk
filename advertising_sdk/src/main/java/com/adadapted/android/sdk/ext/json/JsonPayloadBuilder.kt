package com.adadapted.android.sdk.ext.json

import android.util.Log
import com.adadapted.android.sdk.core.addit.PayloadEvent
import com.adadapted.android.sdk.core.device.DeviceInfo
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.Date

class JsonPayloadBuilder {
    private var wrapper: JSONObject

    fun buildRequest(deviceInfo: DeviceInfo): JSONObject {
        wrapper = buildEventWrapper(deviceInfo)
        val request = JSONObject()
        try {
            request.put(APP_ID, deviceInfo.appId)
            request.put(UDID, deviceInfo.udid)
            request.put(BUNDLE_ID, deviceInfo.bundleId)
            request.put(BUNDLE_VERSION, deviceInfo.bundleVersion)
            request.put(OS, deviceInfo.os)
            request.put(OSV, deviceInfo.osv)
            request.put(DEVICE, deviceInfo.device)
            request.put(SDK_VERSION, deviceInfo.sdkVersion)
            request.put(TIMESTAMP, Date().time)
        } catch (ex: JSONException) {
            Log.w(LOGTAG, "Problem building App Event JSON", ex)
        }
        return request
    }

    private fun buildEventWrapper(deviceInfo: DeviceInfo?): JSONObject {
        val wrapper = JSONObject()
        if (deviceInfo != null) {
            try {
                wrapper.put(APP_ID, deviceInfo.appId)
                wrapper.put(UDID, deviceInfo.udid)
                wrapper.put(BUNDLE_ID, deviceInfo.bundleId)
                wrapper.put(BUNDLE_VERSION, deviceInfo.bundleVersion)
                wrapper.put(OS, deviceInfo.os)
                wrapper.put(OSV, deviceInfo.osv)
                wrapper.put(DEVICE, deviceInfo.device)
                wrapper.put(SDK_VERSION, deviceInfo.sdkVersion)
            } catch (ex: JSONException) {
                Log.w(LOGTAG, "Problem building Payload Tracking Wrapper JSON", ex)
            }
        }
        return wrapper
    }

    fun buildEvent(event: PayloadEvent): JSONObject {
        try {
            val evt = JSONObject()
            evt.put(PAYLOAD_ID, event.payloadId)
            evt.put(STATUS, event.status)
            evt.put(EVENT_TIMESTAMP, event.timestamp)
            val tracking = JSONArray()
            tracking.put(evt)
            val json = JSONObject(wrapper.toString())
            json.put(TRACKING, tracking)
            return json
        } catch (ex: JSONException) {
            Log.w(LOGTAG, "Problem building Payload Event JSON", ex)
        }
        return JSONObject()
    }

    companion object {
        private val LOGTAG = JsonPayloadBuilder::class.java.name
        private const val APP_ID = "app_id"
        private const val UDID = "udid"
        private const val BUNDLE_ID = "bundle_id"
        private const val BUNDLE_VERSION = "bundle_version"
        private const val DEVICE = "device"
        private const val OS = "os"
        private const val OSV = "osv"
        private const val TIMESTAMP = "timestamp"
        private const val SDK_VERSION = "sdk_version"
        private const val PAYLOAD_ID = "payload_id"
        private const val STATUS = "status"
        private const val EVENT_TIMESTAMP = "event_timestamp"
        private const val TRACKING = "tracking"
    }

    init {
        wrapper = JSONObject()
    }
}