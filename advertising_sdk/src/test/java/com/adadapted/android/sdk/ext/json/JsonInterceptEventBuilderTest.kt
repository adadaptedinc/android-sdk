package com.adadapted.android.sdk.ext.json

import com.adadapted.android.sdk.core.intercept.InterceptEvent
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Test

class JsonInterceptEventBuilderTest {
    private var mockSession = Session("testId", true, true, 30, 1907245044, mutableMapOf())
    private var testJsonInterceptEventBuilder = JsonInterceptEventBuilder()

    @Test
    fun marshalEvents() {
        val interceptEvents = setOf(InterceptEvent("testSearchId", "testEvent", "testInput", "testTermId", "testTerm"))
        val resultJsonObject = testJsonInterceptEventBuilder.marshalEvents(mockSession, interceptEvents)
        assertEquals("testId", resultJsonObject.get("session_id"))
        assertEquals("testSearchId", (resultJsonObject.getJSONArray("events").get(0) as JSONObject).get("search_id"))
    }
}