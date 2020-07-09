package com.adadapted.android.sdk.ext.json

import com.adadapted.android.sdk.core.device.DeviceInfo
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class JsonSessionBuilderTest {
    private var mockDeviceInfo = DeviceInfo()
    private lateinit var testJsonSessionBuilder: JsonSessionBuilder

    @Before
    fun setup() {
        testJsonSessionBuilder = JsonSessionBuilder(mockDeviceInfo)
    }

    @Test
    fun buildSession() {
        val testJsonObject = JSONObject()
        testJsonObject.put("session_id", "testSessionId")
                .put("will_serve_ads", "true")
                .put("active_campaigns", "true")
                .put("polling_interval_ms", "5")
                .put("session_expires_at", "10000")

        val sessionResult = testJsonSessionBuilder.buildSession(testJsonObject)

        assertEquals("testSessionId", sessionResult.id)
    }

    @Test
    fun buildSessionWithZones() {

        val testJsonZones = JSONObject()
        val zoneJsonObject = JSONObject()
        zoneJsonObject.put("port_height", "180").put("port_width", "80")

        val payloadItemJson = JSONObject()
                .put("product_title", "testProduct")
                .put("product_brand", "testBrand")
                .put("product_category", "testCat")
                .put("product_barcode", "testBarcode")
                .put("product_sku", "testSku")
                .put("product_discount", "testDiscount")
                .put("product_image", "testImage")
        val payloadListJson = JSONObject().put("detailed_list_items", JSONArray().put(payloadItemJson))

        val testJsonArray = JSONArray()
        testJsonArray.put(JSONObject()
                .put("type", "html")
                .put("refresh_time", "5")
                .put("ad_id", "testAdId")
                .put("action_type", "c")
                .put("impression_id", "testImpId")
                .put("creative_url", "testCreativeUrl")
                .put("action_path", "testPath")
                .put("tracking_html", "testTrackingHtml")
                .put("payload", payloadListJson))
        zoneJsonObject.put("ads", testJsonArray)
        testJsonZones.put("zone1", zoneJsonObject)

        val testJsonObject = JSONObject()
        testJsonObject.put("session_id", "testSessionId")
                .put("will_serve_ads", "true")
                .put("active_campaigns", "true")
                .put("polling_interval_ms", "5")
                .put("session_expires_at", "10000")
                .put("zones", testJsonZones)

        val sessionResult = testJsonSessionBuilder.buildSession(testJsonObject)

        assertEquals("testSessionId", sessionResult.id)
        assertEquals("zone1", sessionResult.getZone("zone1").id)
    }
}