package com.adadapted.android.sdk.ext.json

import com.adadapted.android.sdk.core.ad.AdEvent
import com.adadapted.android.sdk.core.device.DeviceInfo
import com.adadapted.android.sdk.core.session.Session
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.util.Date

class JsonAdEventBuilderTest {
    private var mockSession = Session(DeviceInfo(), "testId", true, true, 30, Date(1907245044), mutableMapOf())
    private var adEvents = mutableSetOf<AdEvent>()
    private var testAdEventBuilder = JsonAdEventBuilder()

    @Before
    fun setup() {
        adEvents.add(AdEvent("adId", "zoneId", "impressionId", "eventType", 1907245044))
    }

    @Test
    fun buildEvents() {
        val resultJsonObject = testAdEventBuilder.marshalEvents(mockSession, adEvents)
        assertEquals("testId", resultJsonObject.get("session_id"))
        assertEquals("adId", (resultJsonObject.getJSONArray("events").get(0) as JSONObject).get("ad_id"))
    }
}