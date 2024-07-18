package com.adadapted.android.sdk.core.payload

import com.adadapted.android.sdk.core.device.DeviceInfo
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import java.util.Date

object PayloadRequestBuilder {
    fun buildRequest(deviceInfo: DeviceInfo): PayloadRequest {
        deviceInfo.run {
            return PayloadRequest(
                appId,
                udid,
                bundleId,
                bundleVersion,
                deviceName,
                os,
                osv,
                sdkVersion,
                Date().time / 1000
            )
        }
    }

    fun buildEventRequest(deviceInfo: DeviceInfo, event: PayloadEvent): PayloadEventRequest {
        val tracking = JsonArray(
            listOf(
                JsonObject(
                    mapOf(
                        "payload_id" to JsonPrimitive(event.payloadId),
                        "status" to JsonPrimitive(event.status),
                        "event_timestamp" to JsonPrimitive(event.timestamp)
                    )
                )
            )
        )
        deviceInfo.run {
            return PayloadEventRequest(
                appId,
                udid,
                bundleId,
                bundleVersion,
                deviceName,
                os,
                osv,
                sdkVersion,
                tracking
            )
        }
    }
}