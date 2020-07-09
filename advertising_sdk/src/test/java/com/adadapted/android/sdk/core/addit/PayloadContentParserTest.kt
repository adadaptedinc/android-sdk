package com.adadapted.android.sdk.core.addit

import androidx.test.platform.app.InstrumentationRegistry
import com.adadapted.android.sdk.core.concurrency.TransporterCoroutineScope
import com.adadapted.android.sdk.core.device.DeviceInfoClient
import com.adadapted.android.sdk.core.device.DeviceInfoClientTest
import com.adadapted.android.sdk.core.event.AppEventClient
import com.adadapted.android.sdk.core.session.SessionClient
import com.adadapted.android.sdk.tools.TestTransporter
import com.nhaarman.mockitokotlin2.mock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.setMain
import org.json.JSONObject
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class PayloadContentParserTest {
    private val JSON_STRING = "{\n" +
            "\t\"payloads\": [{\n" +
            "\t\t\"payload_id\": \"7498E63F-BF44-466B-A391-8B6721C32FF7\",\n" +
            "\t\t\"payload_message\": \"Sample Product\",\n" +
            "\t\t\"payload_image\": \"\",\n" +
            "\t\t\"campaign_id\": \"254\",\n" +
            "\t\t\"app_id\": \"droidrecipe\",\n" +
            "\t\t\"expire_seconds\": 604800,\n" +
            "\t\t\"detailed_list_items\": [{\n" +
            "\t\t\t\"tracking_id\": \"D0699892-BF9E-4E35-B92D-C5D7F2D6E9AD\",\n" +
            "\t\t\t\"product_title\": \"Sample Product\",\n" +
            "\t\t\t\"product_brand\": \"Brand\",\n" +
            "\t\t\t\"product_category\": \"\",\n" +
            "\t\t\t\"product_barcode\": \"67\",\n" +
            "\t\t\t\"product_sku\": \"\",\n" +
            "\t\t\t\"product_discount\": \"\",\n" +
            "\t\t\t\"product_image\": \"https:\\/\\/images.adadapted.com\\/\"\n" +
            "\t\t}]\n" +
            "\t}]\n" +
            "}"

    @Before
    fun setUp() {
        val testTransporter = TestCoroutineDispatcher()
        val testTransporterScope: TransporterCoroutineScope = TestTransporter(testTransporter)
        Dispatchers.setMain(testTransporter)
        DeviceInfoClient.createInstance(InstrumentationRegistry.getInstrumentation().targetContext,"", false, HashMap(), DeviceInfoClientTest.Companion::requestAdvertisingIdInfo, testTransporterScope)
        SessionClient.createInstance(mock(), mock())
        AppEventClient.createInstance(mock(), mock())
        PayloadClient.createInstance(mock(), AppEventClient.getInstance(), mock())
    }

    @Test
    fun parse_null_Json_returns_none() {
        val parser = PayloadContentParser()
        val content = parser.parse(null)
        Assert.assertEquals(0, content.size.toLong())
    }

    @Test
    fun parse() {
        val json = JSONObject(JSON_STRING)
        val parser = PayloadContentParser()
        val content = parser.parse(json)

        assert(content.first().payloadId == "7498E63F-BF44-466B-A391-8B6721C32FF7")
        assert(content.first().getItems().first().trackingId == "D0699892-BF9E-4E35-B92D-C5D7F2D6E9AD")
        assert(content.first().getItems().first().productUpc == "67")
    }
}