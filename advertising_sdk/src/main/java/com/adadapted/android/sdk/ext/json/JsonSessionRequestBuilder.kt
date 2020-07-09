package com.adadapted.android.sdk.ext.json

import android.util.Log
import com.adadapted.android.sdk.core.device.DeviceInfo
import org.json.JSONException
import org.json.JSONObject
import java.util.Date

class JsonSessionRequestBuilder {
    fun buildSessionInitRequest(deviceInfo: DeviceInfo): JSONObject {
        val json = JSONObject()
        try {
            json.put(APP_ID, deviceInfo.appId)
            json.put(UDID, deviceInfo.udid)
            json.put(BUNDLE_ID, deviceInfo.bundleId)
            json.put(BUNDLE_VERSION, deviceInfo.bundleVersion)
            json.put(DEVICE_NAME, deviceInfo.device)
            json.put(DEVICE_UDID, deviceInfo.deviceUdid)
            json.put(DEVICE_OS, deviceInfo.os)
            json.put(DEVICE_OSV, deviceInfo.osv)
            json.put(DEVICE_LOCALE, deviceInfo.locale)
            json.put(DEVICE_TIMEZONE, deviceInfo.timezone)
            json.put(DEVICE_CARRIER, deviceInfo.carrier)
            json.put(DEVICE_HEIGHT, deviceInfo.dh)
            json.put(DEVICE_WIDTH, deviceInfo.dw)
            json.put(DEVICE_DENSITY, Integer.valueOf(deviceInfo.density).toString())
            json.put(ALLOW_RETARGETING, deviceInfo.isAllowRetargetingEnabled)
            json.put(CREATED_AT, Date().time)
            json.put(SDK_VERSION, deviceInfo.sdkVersion)
            json.put(PARAMS, JSONObject(deviceInfo.params))
        } catch (ex: JSONException) {
            Log.d(LOGTAG, "Problem converting to JSON.", ex)
        }
        return json
    }

    companion object {
        private val LOGTAG = JsonSessionRequestBuilder::class.java.name
        private const val APP_ID = "app_id"
        private const val UDID = "udid"
        private const val BUNDLE_ID = "bundle_id"
        private const val BUNDLE_VERSION = "bundle_version"
        private const val DEVICE_NAME = "device_name"
        private const val DEVICE_UDID = "device_udid"
        private const val DEVICE_OS = "device_os"
        private const val DEVICE_OSV = "device_osv"
        private const val DEVICE_LOCALE = "device_locale"
        private const val DEVICE_TIMEZONE = "device_timezone"
        private const val DEVICE_CARRIER = "device_carrier"
        private const val DEVICE_HEIGHT = "device_height"
        private const val DEVICE_WIDTH = "device_width"
        private const val DEVICE_DENSITY = "device_density"
        private const val ALLOW_RETARGETING = "allow_retargeting"
        private const val CREATED_AT = "created_at"
        private const val SDK_VERSION = "sdk_version"
        private const val PARAMS = "params"
    }
}