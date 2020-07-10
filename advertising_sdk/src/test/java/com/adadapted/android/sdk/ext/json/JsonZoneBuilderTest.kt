package com.adadapted.android.sdk.ext.json

import androidx.test.platform.app.InstrumentationRegistry
import com.adadapted.android.sdk.core.concurrency.TransporterCoroutineScope
import com.adadapted.android.sdk.core.device.DeviceInfoClient
import com.adadapted.android.sdk.core.device.DeviceInfoClientTest
import com.adadapted.android.sdk.core.event.AppEventClient
import com.adadapted.android.sdk.core.event.TestAppEventSink
import com.adadapted.android.sdk.core.session.SessionClient
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
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class JsonZoneBuilderTest {
    private lateinit var testJsonZoneBuilder: JsonZoneBuilder
    private var testJsonZones = JSONObject()
    private var testTransporter = TestCoroutineDispatcher()
    private val testTransporterScope: TransporterCoroutineScope = TestTransporter(testTransporter)
    private var testAppEventSink = TestAppEventSink()

    @Before
    fun setup() {
        Dispatchers.setMain(testTransporter)
        DeviceInfoClient.createInstance(InstrumentationRegistry.getInstrumentation().targetContext,"", false, HashMap(), DeviceInfoClientTest.Companion::requestAdvertisingIdInfo, testTransporterScope)
        SessionClient.createInstance(mock(), mock())
        AppEventClient.createInstance(testAppEventSink, testTransporterScope)

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
        testJsonZoneBuilder = JsonZoneBuilder(5f)
    }


    @Test
    fun buildZones() {
        val resultZoneMap = testJsonZoneBuilder.buildZones(testJsonZones)
        assertEquals("zone1", resultZoneMap["zone1"]?.id)
    }
}