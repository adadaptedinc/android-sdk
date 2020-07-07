package com.adadapted.android.sdk.core.ad

import androidx.test.platform.app.InstrumentationRegistry
import com.adadapted.android.sdk.core.atl.AddToListContent
import com.adadapted.android.sdk.core.atl.AddToListItem
import com.adadapted.android.sdk.core.concurrency.TransporterCoroutineScope
import com.adadapted.android.sdk.core.device.DeviceInfo
import com.adadapted.android.sdk.core.device.DeviceInfoClient
import com.adadapted.android.sdk.core.device.DeviceInfoClientTest
import com.adadapted.android.sdk.core.event.AppEventClient
import com.adadapted.android.sdk.core.event.TestAppEventSink
import com.adadapted.android.sdk.core.session.Session
import com.adadapted.android.sdk.core.session.SessionClient
import com.adadapted.android.sdk.tools.TestAdEventSink
import com.adadapted.android.sdk.tools.TestTransporter
import com.adadapted.android.sdk.ui.messaging.AdContentListener
import com.adadapted.android.sdk.ui.messaging.AdContentPublisher
import com.nhaarman.mockitokotlin2.mock
import junit.framework.TestCase.assertNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.util.Date

@RunWith(RobolectricTestRunner::class)
class AdContentPublisherTest {

    private var mockImpressionIdCounter = mock<Counter>()
    private var mockAdEventSink = mock<TestAdEventSink>()
    private var testContext = InstrumentationRegistry.getInstrumentation().targetContext
    private var testTransporter = TestCoroutineDispatcher()
    private val testTransporterScope: TransporterCoroutineScope = TestTransporter(testTransporter)
    private var testAppEventSink = TestAppEventSink()
    private var mockSession = Session(DeviceInfo(), "testId", true, true, 30, Date(1907245044), mutableMapOf())

    @Before
    fun setup() {
        Dispatchers.setMain(testTransporter)
        DeviceInfoClient.createInstance(testContext,"", false, HashMap(), DeviceInfoClientTest.Companion::requestAdvertisingIdInfo, testTransporterScope)
        SessionClient.createInstance(mock(), mock())
        AdEventClient.createInstance(mockAdEventSink, testTransporterScope, mockImpressionIdCounter)
        AdEventClient.getInstance().onSessionAvailable(mockSession)
        AppEventClient.createInstance(testAppEventSink, testTransporterScope)
    }

    @Test
    fun listenerIsAddedAndPublished() {
        val testListener = TestAdContentListener()
        AdContentPublisher.getInstance().addListener(testListener)
        AdContentPublisher.getInstance().publishContent(
                "testZoneId",
                AdContent.createAddToListContent(Ad(
                        "adId",
                        "adZoneId",
                        payload = listOf(
                                AddToListItem("track", "title", "brand", "cat", "upc", "sku", "discount", "image")))))
        assertEquals("testZoneId", testListener.resultZoneId)
        assertEquals("adZoneId", (testListener.resultContent as AdContent).zoneId)
    }

    @Test
    fun listenerIsRemovedAndPublished() {
        val testListener = TestAdContentListener()
        AdContentPublisher.getInstance().addListener(testListener)
        AdContentPublisher.getInstance().removeListener(testListener)
        AdContentPublisher.getInstance().publishContent(
                "testZoneId",
                AdContent.createAddToListContent(Ad(
                        "adId",
                        "adZoneId",
                        payload = listOf(
                                AddToListItem("track", "title", "brand", "cat", "upc", "sku", "discount", "image")))))
        assertEquals("", testListener.resultZoneId)
        assertNull(testListener.resultContent)
    }
}

class TestAdContentListener: AdContentListener {
    var resultZoneId = ""
    var resultContent: AddToListContent? = null

    override fun onContentAvailable(zoneId: String, content: AddToListContent) {
        resultZoneId = zoneId
        resultContent = content
    }
}