package com.adadapted.android.sdk.ext.json

import com.adadapted.android.sdk.core.concurrency.TransporterCoroutineScope
import com.adadapted.android.sdk.core.device.DeviceInfo
import com.adadapted.android.sdk.core.device.DeviceInfoClient
import com.adadapted.android.sdk.core.event.AppEventClient
import com.adadapted.android.sdk.core.event.TestAppEventSink
import com.adadapted.android.sdk.core.session.SessionClient
import com.adadapted.android.sdk.tools.TestTransporter
import com.nhaarman.mockitokotlin2.mock
import kotlinx.coroutines.test.TestCoroutineDispatcher
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class JsonSessionBuilderTest {
    private var mockDeviceInfo = DeviceInfo()
    private lateinit var testJsonSessionBuilder: JsonSessionBuilder

    private var testAppEventSink = TestAppEventSink()
    private var testTransporter = TestCoroutineDispatcher()
    private val testTransporterScope: TransporterCoroutineScope = TestTransporter(testTransporter)

    @Before
    fun setup() {
        DeviceInfoClient.createInstance(mock(), "", false, mock(), mock(), mock())
        SessionClient.createInstance(mock(), mock())
        AppEventClient.createInstance(testAppEventSink, testTransporterScope)
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
    fun buildSessionWillNotFail() {
        val testJsonObject = JSONObject()
        testJsonObject.put("session_id", "testSessionId")
                .put("will_serve_ads_fail", "false")
                .put("active_campaigns", "truebad")
                .put("polling_interval_ms", 5)
                .put("session_expires_at", 1000L)
                .put("-(0)*&", "nonsense")

        testJsonSessionBuilder.buildSession(testJsonObject)

        AppEventClient.getInstance().onPublishEvents()
        assert(testAppEventSink.testErrors.isEmpty())
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
        assertEquals(80, sessionResult.getZone("zone1").portWidth)
    }
}