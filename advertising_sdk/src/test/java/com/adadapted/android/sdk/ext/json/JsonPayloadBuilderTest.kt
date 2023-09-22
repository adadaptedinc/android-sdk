package com.adadapted.android.sdk.ext.json

import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class JsonPayloadBuilderTest {
    private var testJsonPayloadBuilder = JsonPayloadBuilder()
    private var configuredJsonObject = JSONObject()


    @Before
    fun setupRequest() {
        val mockDeviceInfo = DeviceInfo()
        mockDeviceInfo.udid = "testUdId"
        mockDeviceInfo.appId = "testAppId"
        mockDeviceInfo.bundleId = "bundleId"
        mockDeviceInfo.bundleVersion = "bundleVersion"
        mockDeviceInfo.osv = "OSV"
        mockDeviceInfo.device = "Device"
        configuredJsonObject = testJsonPayloadBuilder.buildRequest(mockDeviceInfo)
    }

    @Test
    fun buildRequest() {
        assertEquals("testUdId", configuredJsonObject.get("udid"))
    }

    @Test
    fun buildEvent() {
        val mockPayloadEvent = PayloadEvent("testPayloadId", "testStatus")
        val resultJsonObject = testJsonPayloadBuilder.buildEvent(mockPayloadEvent)
        assertEquals("testPayloadId", ((resultJsonObject.get("tracking") as JSONArray).get(0) as JSONObject).get("payload_id"))
    }
}