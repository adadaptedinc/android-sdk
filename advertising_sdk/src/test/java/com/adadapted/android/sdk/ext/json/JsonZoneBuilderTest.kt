package com.adadapted.android.sdk.ext.json

import com.adadapted.android.sdk.core.concurrency.TransporterCoroutineScope
import com.adadapted.android.sdk.core.device.DeviceInfoClient
import com.adadapted.android.sdk.core.event.AppEventClient
import com.adadapted.android.sdk.core.event.TestAppEventSink
import com.adadapted.android.sdk.core.session.SessionClient
import com.adadapted.android.sdk.tools.TestDeviceInfoExtractor
import com.adadapted.android.sdk.tools.TestTransporter
import com.nhaarman.mockitokotlin2.mock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.setMain
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class JsonZoneBuilderTest {
    private lateinit var testJsonZoneBuilder: JsonZoneBuilder
    private var testJsonZones = JSONObject()
    private var testTransporter = TestCoroutineDispatcher()
    private val testTransporterScope: TransporterCoroutineScope = TestTransporter(testTransporter)
    private var testAppEventSink = TestAppEventSink()

    @Before
    fun setup() {
        Dispatchers.setMain(testTransporter)
        DeviceInfoClient.createInstance(mock(),"", false, HashMap(), TestDeviceInfoExtractor(), testTransporterScope)
        SessionClient.createInstance(mock(), mock())
        AppEventClient.createInstance(testAppEventSink, testTransporterScope)
    }

    @Test
    fun buildPortZones() {
        setupZone("port_height", 150, "port_width", -1)
        val resultZoneMap = testJsonZoneBuilder.buildZones(testJsonZones)
        assertEquals("zone1", resultZoneMap["zone1"]?.id)
    }

    @Test
    fun buildLandZones() {
        setupZone("land_height", 150, "land_width", -1)
        val resultZoneMap = testJsonZoneBuilder.buildZones(testJsonZones)
        assertEquals("zone1", resultZoneMap["zone1"]?.id)
    }

    @Test
    fun buildBadPortZones() {
        setupZone("port_height", "badval1", "port_width", "badval2")
        val resultZoneMap = testJsonZoneBuilder.buildZones(testJsonZones)
        assertEquals("zone1", resultZoneMap["zone1"]?.id)
    }

    @Test
    fun buildBadLandZones() {
        setupZone("land_height", "badval1", "land_width", "badval2")
        val resultZoneMap = testJsonZoneBuilder.buildZones(testJsonZones)
        assertEquals("zone1", resultZoneMap["zone1"]?.id)
    }

    @Test
    fun buildReallyBadPortZones() {
        setupZone("port_height", 9.0045, "port_width", " ")
        val resultZoneMap = testJsonZoneBuilder.buildZones(testJsonZones)
        assertEquals("zone1", resultZoneMap["zone1"]?.id)
    }

    private fun setupZone(heightName: String, heightValue: Any, widthName: String, widthValue: Any) {
        val zoneJsonObject = JSONObject()
        zoneJsonObject.put(heightName, heightValue).put(widthName, widthValue)

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
        testJsonZoneBuilder = JsonZoneBuilder(5f)
    }
}