package com.adadapted.android.sdk.ext.json

import com.adadapted.android.sdk.core.addit.PayloadEvent
import com.adadapted.android.sdk.core.device.DeviceInfo
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Test

class JsonPayloadBuilderTest {
    private var testJsonPayloadBuilder = JsonPayloadBuilder()

    @Test
    fun buildRequest() {
        val mockDeviceInfo = DeviceInfo()
        mockDeviceInfo.udid = "testUdId"
        val resultJsonObject = testJsonPayloadBuilder.buildRequest(mockDeviceInfo)
        assertEquals("testUdId", resultJsonObject.get("udid"))
    }

    @Test
    fun buildEvent() {
        val mockPayloadEvent = PayloadEvent("testPayloadId", "testStatus")
        val resultJsonObject = testJsonPayloadBuilder.buildEvent(mockPayloadEvent)
        assertEquals("testPayloadId", ((resultJsonObject.get("tracking") as JSONArray).get(0) as JSONObject).get("payload_id"))
    }
}