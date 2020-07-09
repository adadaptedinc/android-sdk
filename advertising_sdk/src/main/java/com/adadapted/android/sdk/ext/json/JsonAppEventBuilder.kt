package com.adadapted.android.sdk.ext.json

import android.util.Log
import com.adadapted.android.sdk.core.device.DeviceInfo
import com.adadapted.android.sdk.core.event.AppError
import com.adadapted.android.sdk.core.event.AppEvent
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class JsonAppEventBuilder {
    fun buildWrapper(deviceInfo: DeviceInfo): JSONObject {
        val wrapper = JSONObject()
        try {
            wrapper.put(APP_ID, deviceInfo.appId)
            wrapper.put(UDID, deviceInfo.udid)
            wrapper.put(DEVICE_UDID, deviceInfo.deviceUdid)
            wrapper.put(BUNDLE_ID, deviceInfo.bundleId)
            wrapper.put(BUNDLE_VERSION, deviceInfo.bundleVersion)
            wrapper.put(ALLOW_RETARGETING, if (deviceInfo.isAllowRetargetingEnabled) 1 else 0)
            wrapper.put(OS, deviceInfo.os)
            wrapper.put(OSV, deviceInfo.osv)
            wrapper.put(DEVICE, deviceInfo.device)
            wrapper.put(CARRIER, deviceInfo.carrier)
            wrapper.put(DW, deviceInfo.dw)
            wrapper.put(DH, deviceInfo.dh)
            wrapper.put(DENSITY, Integer.toString(deviceInfo.density))
            wrapper.put(TIMEZONE, deviceInfo.timezone)
            wrapper.put(LOCALE, deviceInfo.locale)
            wrapper.put(SDK_VERSION, deviceInfo.sdkVersion)
        } catch (ex: JSONException) {
            Log.d(LOGTAG, "Problem converting to JSON.", ex)
        }
        return wrapper
    }

    fun buildEventItem(wrapper: JSONObject?, events: Set<AppEvent>): JSONObject? {
        try {
            val items = JSONArray()
            for (event in events) {
                val item = JSONObject()
                item.put(APP_EVENT_SOURCE, event.type)
                item.put(APP_EVENT_NAME, event.name)
                item.put(APP_EVENT_TIMESTAMP, event.datetime)
                item.put(APP_EVENT_PARAMS, buildParams(event.params))
                items.put(item)
            }
            wrapper?.put(APP_EVENTS, items)
        } catch (ex: JSONException) {
            Log.d(LOGTAG, "Problem converting to JSON.", ex)
        }
        return wrapper
    }

    fun buildErrorItem(wrapper: JSONObject?, errors: Set<AppError>): JSONObject? {
        val item = JSONObject()
        try {
            val json = JSONArray()
            for (error in errors) {
                item.put(APP_ERROR_CODE, error.code)
                item.put(APP_ERROR_MESSAGE, error.message)
                item.put(APP_ERROR_TIMESTAMP, error.datetime)
                item.put(APP_ERROR_PARAMS, buildParams(error.params))
                json.put(item)
            }
            wrapper?.put(APP_ERRORS, json)
        } catch (ex: JSONException) {
            Log.w(LOGTAG, "Problem building App Error JSON")
        }
        return wrapper
    }

    @Throws(JSONException::class)
    private fun buildParams(params: Map<String, String>): JSONObject {
        val p = JSONObject()
        for (key in params.keys) {
            p.put(key, params[key])
        }
        return p
    }

    companion object {
        private val LOGTAG = JsonAppEventBuilder::class.java.name
        private const val APP_ID = "app_id"
        private const val UDID = "udid"
        private const val BUNDLE_ID = "bundle_id"
        private const val BUNDLE_VERSION = "bundle_version"
        private const val DEVICE = "device"
        private const val DEVICE_UDID = "device_udid"
        private const val OS = "os"
        private const val OSV = "osv"
        private const val LOCALE = "locale"
        private const val TIMEZONE = "timezone"
        private const val CARRIER = "carrier"
        private const val DH = "dh"
        private const val DW = "dw"
        private const val DENSITY = "density"
        private const val ALLOW_RETARGETING = "allow_retargeting"
        private const val SDK_VERSION = "sdk_version"
        private const val APP_ERRORS = "errors"
        private const val APP_ERROR_CODE = "error_code"
        private const val APP_ERROR_MESSAGE = "error_message"
        private const val APP_ERROR_TIMESTAMP = "error_timestamp"
        private const val APP_ERROR_PARAMS = "error_params"
        private const val APP_EVENTS = "events"
        private const val APP_EVENT_SOURCE = "event_source"
        private const val APP_EVENT_NAME = "event_name"
        private const val APP_EVENT_TIMESTAMP = "event_timestamp"
        private const val APP_EVENT_PARAMS = "event_params"
    }
}