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

class JsonAdBuilderTest {
    private var testJsonArray = JSONArray()
    private var testJsonAdBuilder = JsonAdBuilder()
    private var testTransporter = TestCoroutineDispatcher()
    private val testTransporterScope: TransporterCoroutineScope = TestTransporter(testTransporter)
    private var testAppEventSink = TestAppEventSink()

    @Before
    fun setup() {
        Dispatchers.setMain(testTransporter)
        DeviceInfoClient.createInstance(mock(),"", false, HashMap(), TestDeviceInfoExtractor(), testTransporterScope)
        SessionClient.createInstance(mock(), mock())
        AppEventClient.createInstance(testAppEventSink, testTransporterScope)

        val payloadItemJson = JSONObject()
                .put("product_title", "testProduct")
                .put("product_brand", "testBrand")
                .put("product_category", "testCat")
                .put("product_barcode", "testBarcode")
                .put("product_sku", "testSku")
                .put("product_discount", "testDiscount")
                .put("product_image", "testImage")

        val payloadListJson = JSONObject().put("detailed_list_items", JSONArray().put(payloadItemJson))

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
    }

    @Test
    fun buildAds() {
        val testAds = testJsonAdBuilder.buildAds("testZoneId", testJsonArray)
        assertEquals("testAdId", testAds.first().id)
        assertEquals("testProduct", testAds.first().payload.first().title)
        assertEquals(5, testAds.first().refreshTime)
    }
}