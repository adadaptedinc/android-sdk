package com.adadapted.android.sdk.core.addit

import android.net.Uri
import com.adadapted.android.sdk.constants.EventStrings
import com.adadapted.android.sdk.core.atl.AddToListContent
import com.adadapted.android.sdk.core.atl.AdditContent
import com.adadapted.android.sdk.core.concurrency.TransporterCoroutineScope
import com.adadapted.android.sdk.core.deeplink.DeeplinkContentParser
import com.adadapted.android.sdk.core.device.DeviceInfoClient
import com.adadapted.android.sdk.core.event.EventClient
import com.adadapted.android.sdk.core.payload.PayloadClient
import com.adadapted.android.sdk.core.session.SessionClient
import com.adadapted.android.sdk.tools.TestDeviceInfoExtractor
import com.adadapted.android.sdk.tools.TestEventAdapter
import com.adadapted.android.sdk.tools.TestTransporter
import com.nhaarman.mockitokotlin2.mock
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.lang.Exception

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class DeeplinkContentParserTest {
    private var testTransporter = UnconfinedTestDispatcher()
    private val testTransporterScope: TransporterCoroutineScope = TestTransporter(testTransporter)

    @Before
    fun setup() {
        DeviceInfoClient.createInstance("", false, HashMap(), "", TestDeviceInfoExtractor(), testTransporterScope)
        SessionClient.onStart(mock())
        EventClient.createInstance(TestEventAdapter, testTransporterScope)
        PayloadClient.createInstance(mock(), EventClient, mock())
    }

    @Test
    fun parse() {
        val uri = Uri.parse(TEST_URL_STRING_ITEMS)
        val parser = DeeplinkContentParser()
        val content = parser.parse(uri)
        Assert.assertEquals("7498E63F-BF44-466B-A391-8B6721C32FF7", content.payloadId)
        Assert.assertEquals("Sample Product", content.message)
        Assert.assertEquals("", content.image)
        Assert.assertEquals(AdditContent.AdditSources.DEEPLINK, content.additSource)
        Assert.assertEquals(AddToListContent.Sources.OUT_OF_APP, content.getSource())
        Assert.assertEquals(1, content.getItems().size.toLong())
    }

    @Test
    fun fail() {
        val parser = DeeplinkContentParser()

        try {
            parser.parse(null)
            Assert.assertEquals(EventStrings.ADDIT_NO_DEEPLINK_RECEIVED, TestEventAdapter.testSdkErrors.first().code)
            Assert.fail()
        } catch (ex: Exception) {
            //success
        }
    }

    @Test
    fun failWithParseFailure() {
        try {
            val uri = Uri.parse(TEST_FAIL_URL_STRING)
            val parser = DeeplinkContentParser()
            parser.parse(uri)
            Assert.assertEquals(EventStrings.ADDIT_PAYLOAD_PARSE_FAILED, TestEventAdapter.testSdkErrors.first().code)
        } catch (ex: Exception) {
            //success
        }
    }

    @Test
    fun failWithPayloadFailure() {
        try {
            val uri = Uri.parse(TEST_UNKNOWN_URL_STRING)
            val parser = DeeplinkContentParser()
            parser.parse(uri)
            Assert.assertEquals(EventStrings.ADDIT_PAYLOAD_PARSE_FAILED, TestEventAdapter.testSdkErrors.first().code)
        } catch (ex: Exception) {
            //success
        }
    }

    companion object {
        private const val TEST_URL_STRING_ITEMS = "droidrecipe://adadapted.com/addit_add_list_items?data=eyJwYXlsb2FkX2lkIjoiNzQ5OEU2M0YtQkY0NC00NjZCLUEzOTEtOEI2NzIxQzMyRkY3IiwicGF5bG9hZF9tZXNzYWdlIjoiU2FtcGxlIFByb2R1Y3QiLCJwYXlsb2FkX2ltYWdlIjoiIiwiY2FtcGFpZ25faWQiOiIyNTQiLCJhcHBfaWQiOiJkcm9pZHJlY2lwZSIsImV4cGlyZV9zZWNvbmRzIjo2MDQ4MDAsImRldGFpbGVkX2xpc3RfaXRlbXMiOlt7InRyYWNraW5nX2lkIjoiRDA2OTk4OTItQkY5RS00RTM1LUI5MkQtQzVEN0YyRDZFOUFEIiwicHJvZHVjdF90aXRsZSI6IlNhbXBsZSBQcm9kdWN0IiwicHJvZHVjdF9icmFuZCI6IkJyYW5kIiwicHJvZHVjdF9jYXRlZ29yeSI6IiIsInByb2R1Y3RfYmFyY29kZSI6IjAiLCJwcm9kdWN0X3NrdSI6IiIsInByb2R1Y3RfZGlzY291bnQiOiIiLCJwcm9kdWN0X2ltYWdlIjoiaHR0cHM6XC9cL2ltYWdlcy5hZGFkYXB0ZWQuY29tXC8ifV19"
        private const val TEST_FAIL_URL_STRING = "droidrecipe://adadapted.com/addit_add_list_items?data=UNKNOWN"
        private const val TEST_UNKNOWN_URL_STRING = "droidrecipe://adadapted.com/unknown"
    }
}
