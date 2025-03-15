package com.adadapted.android.sdk.core.event

import com.adadapted.android.sdk.core.device.DeviceInfo

object EventRequestBuilder {
    fun buildAdEventRequest(sessionId: String, deviceInfo: DeviceInfo, adEvents: Set<AdEvent>): AdEventRequest {
        deviceInfo.run {
            return AdEventRequest(
                sessionId,
                deviceInfo.appId,
                deviceInfo.udid,
                deviceInfo.sdkVersion,
                adEvents.toList()
            )
        }
    }

    fun buildEventRequest(sessionId: String, deviceInfo: DeviceInfo, sdkEvents: Set<SdkEvent> = setOf(), sdkErrors: Set<SdkError> = setOf()): EventRequest {
        deviceInfo.run {
            return EventRequest(
                sessionId,
                appId,
                bundleId,
                bundleVersion,
                udid,
                deviceName,
                deviceUdid,
                os,
                osv,
                locale,
                timezone,
                carrier,
                dw,
                dh,
                density,
                sdkVersion,
                if (isAllowRetargetingEnabled) 1 else 0,
                sdkEvents.toList(),
                sdkErrors.toList()
            )
        }
    }
}