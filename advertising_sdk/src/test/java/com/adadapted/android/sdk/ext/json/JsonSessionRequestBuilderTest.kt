package com.adadapted.android.sdk.ext.json

import com.adadapted.android.sdk.core.http.HttpAppEventSinkTest
import org.junit.Assert.assertEquals
import org.junit.Test

class JsonSessionRequestBuilderTest {

    @Test
    fun buildSessionInitRequest() {
        val mockDeviceInfo = HttpAppEventSinkTest.generateMockDeviceInfo()
        val testJsonSessionRequestBuilder = JsonSessionRequestBuilder()

        val resultRequest = testJsonSessionRequestBuilder.buildSessionInitRequest(mockDeviceInfo)
        assertEquals("testUdid", resultRequest.get("udid"))
        assertEquals(true, resultRequest.get("allow_retargeting"))
        assertEquals("testAppId", resultRequest.get("app_id"))
        assertEquals("testBundleId", resultRequest.get("bundle_id"))
        assertEquals("testBundleVersion", resultRequest.get("bundle_version"))
        assertEquals("testDevice", resultRequest.get("device_name"))
        assertEquals("DeviceUdId", resultRequest.get("device_udid"))
        assertEquals("Android", resultRequest.get("device_os"))
        assertEquals("testOsv", resultRequest.get("device_osv"))
        assertEquals("testLocale", resultRequest.get("device_locale"))
        assertEquals("testTimeZone", resultRequest.get("device_timezone"))
        assertEquals("testCarrier", resultRequest.get("device_carrier"))
        assertEquals(0, resultRequest.get("device_height"))
        assertEquals(0, resultRequest.get("device_width"))
        assertEquals("0", resultRequest.get("device_density"))
        assertEquals("testLocale", resultRequest.get("device_locale"))
    }
}