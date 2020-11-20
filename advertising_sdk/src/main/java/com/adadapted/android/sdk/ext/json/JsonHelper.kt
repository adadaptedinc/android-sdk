package com.adadapted.android.sdk.ext.json

import org.json.JSONObject

class JsonHelper {
    companion object {
        fun tryGetIntFromJson(json: JSONObject, value: String, defaultValue: Int = -1): Int {
            return try {
                json.getInt(value)
            } catch (ex: java.lang.Exception) {
                defaultValue
            }
        }

        fun tryGetLongFromJson(json: JSONObject, value: String): Long {
            return try {
                json.getLong(value)
            } catch (ex: java.lang.Exception) {
                0L
            }
        }
    }
}