package com.adadapted.android.sdk.core.event

import com.adadapted.android.sdk.core.session.Session

object EventRequestBuilder {
    fun buildAdEventRequest(session: Session, adEvents: Set<AdEvent>): AdEventRequest {
        session.run {
            return AdEventRequest(
                id,
                deviceInfo.appId,
                deviceInfo.udid,
                deviceInfo.sdkVersion,
                adEvents.toList()
            )
        }
    }

    fun buildEventRequest(session: Session, sdkEvents: Set<SdkEvent> = setOf(), sdkErrors: Set<SdkError> = setOf()): EventRequest {
        session.deviceInfo.run {
            return EventRequest(
                session.id,
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