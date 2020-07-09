package com.adadapted.android.sdk.ext.json

import com.adadapted.android.sdk.core.device.DeviceInfo
import com.adadapted.android.sdk.core.intercept.InterceptEvent
import com.adadapted.android.sdk.core.session.Session
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Date

class JsonInterceptEventBuilderTest {
    private var mockSession = Session(DeviceInfo(), "testId", true, true, 30, Date(1907245044), mutableMapOf())
    private var testJsonInterceptEventBuilder = JsonInterceptEventBuilder()

    @Test
    fun marshalEvents() {
        val interceptEvents = setOf(InterceptEvent("testSearchId", "testEvent", "testInput", "testTermId", "testTerm"))
        val resultJsonObject = testJsonInterceptEventBuilder.marshalEvents(mockSession, interceptEvents)
        assertEquals("testId", resultJsonObject.get("session_id"))
        assertEquals("testSearchId", (resultJsonObject.getJSONArray("events").get(0) as JSONObject).get("search_id"))
    }
}