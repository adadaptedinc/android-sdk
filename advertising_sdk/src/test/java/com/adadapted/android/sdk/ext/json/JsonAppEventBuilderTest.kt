package com.adadapted.android.sdk.ext.json

import com.adadapted.android.sdk.core.device.DeviceInfo
import com.adadapted.android.sdk.core.event.AppError
import com.adadapted.android.sdk.core.event.AppEvent
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Test

class JsonAppEventBuilderTest {
    private var testJsonAppEventBuilder = JsonAppEventBuilder()

    @Test
    fun buildWrapper() {
        val testDeviceInfo = DeviceInfo()
        testDeviceInfo.deviceUdid = "testDeviceId"

        val wrapper = testJsonAppEventBuilder.buildWrapper(testDeviceInfo)
        assertEquals("testDeviceId", wrapper.get("device_udid"))
    }

    @Test
    fun buildErrorItem() {
        val testDeviceInfo = DeviceInfo()
        testDeviceInfo.deviceUdid = "testDeviceId"

        val wrapper = testJsonAppEventBuilder.buildWrapper(testDeviceInfo)
        val errors = setOf(AppError("testErrorCode", "errMessage", mapOf()))

        val resultJsonObject = testJsonAppEventBuilder.buildErrorItem(wrapper, errors)
        assertEquals("testErrorCode", (resultJsonObject?.getJSONArray("errors")?.get(0) as JSONObject).get("error_code"))
    }

    @Test
    fun buildEventItem() {
        val testDeviceInfo = DeviceInfo()
        testDeviceInfo.deviceUdid = "testDeviceId"

        val wrapper = testJsonAppEventBuilder.buildWrapper(testDeviceInfo)
        val errors = setOf(AppEvent("testType", "testEvent", mapOf()))

        val resultJsonObject = testJsonAppEventBuilder.buildEventItem(wrapper, errors)
        assertEquals("testEvent", (resultJsonObject?.getJSONArray("events")?.get(0) as JSONObject).get("event_name"))
    }
}