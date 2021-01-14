package com.adadapted.android.sdk.core.ad

import com.adadapted.android.sdk.config.EventStrings
import com.adadapted.android.sdk.core.atl.AddToListItem
import com.adadapted.android.sdk.core.concurrency.TransporterCoroutineScope
import com.adadapted.android.sdk.core.device.DeviceInfoClient
import com.adadapted.android.sdk.core.event.AppEventClient
import com.adadapted.android.sdk.core.event.TestAppEventSink
import com.adadapted.android.sdk.core.session.Session
import com.adadapted.android.sdk.core.session.SessionClient
import com.adadapted.android.sdk.ext.models.Payload
import com.adadapted.android.sdk.tools.TestAdEventSink
import com.adadapted.android.sdk.tools.TestDeviceInfoExtractor
import com.adadapted.android.sdk.tools.TestTransporter
import com.nhaarman.mockitokotlin2.mock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.setMain
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertTrue
import kotlinx.coroutines.test.resetMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.util.Date
import kotlin.collections.HashMap

@RunWith(RobolectricTestRunner::class)
class AdContentTest {
    private var testAdEventSink = TestAdEventSink()
    private var testAppEventSink = TestAppEventSink()
    private var testTransporter = TestCoroutineDispatcher()
    private val testTransporterScope: TransporterCoroutineScope = TestTransporter(testTransporter)
    private var mockSession = Session("testId", true, true, 30, 1907245044, mutableMapOf())
    private var testAddTolistItems = listOf(AddToListItem("testTrackingId", "title", "brand", "cat", "upc", "sku", "discount", "image"))

    @Before
    fun setup() {
        Dispatchers.setMain(testTransporter)
        DeviceInfoClient.createInstance(mock(),"", false, HashMap(), TestDeviceInfoExtractor(), testTransporterScope)
        SessionClient.createInstance(mock(), mock())
        AdEventClient.createInstance(testAdEventSink, testTransporterScope)
        AdEventClient.getInstance().onSessionAvailable(mockSession)
        AppEventClient.createInstance(testAppEventSink, testTransporterScope)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testTransporter.cleanupTestCoroutines()
    }

    @Test
    fun createAdContent() {
        val testAdContent = AdContent.createAddToListContent(Ad("adContentId", "testZoneId"))
        assertEquals(0, testAdContent.type)
        assertEquals("testZoneId", testAdContent.zoneId)
    }

    @Test
    fun acknowledge() {
        val testAdContent = AdContent.createAddToListContent(Ad("adContentId", "testZoneId"))
        testAdEventSink.testEvents = mutableSetOf()
        testAdContent.acknowledge()
        AdEventClient.getInstance().onPublishEvents()
        assertEquals(AdEvent.Types.INTERACTION, testAdEventSink.testEvents.first().eventType)
        assertEquals("testZoneId", testAdEventSink.testEvents.first().zoneId)
        assertEquals("adContentId", testAdEventSink.testEvents.first().adId)
    }

    @Test
    fun itemAcknowledge() {
        val testAdContent = AdContent.createAddToListContent(Ad("adContentId", "testZoneId", payload = Payload(testAddTolistItems)))
        testAdEventSink.testEvents = mutableSetOf()
        testAdContent.itemAcknowledge(testAdContent.getItems().first())
        AdEventClient.getInstance().onPublishEvents()
        AppEventClient.getInstance().onPublishEvents()
        assert(testAdEventSink.testEvents.any { event -> event.eventType == AdEvent.Types.INTERACTION })
        assert(testAppEventSink.testEvents.any { event -> event.name == EventStrings.ATL_ITEM_ADDED_TO_LIST })
        assertEquals("adContentId", testAdEventSink.testEvents.first().adId)
        assertEquals("testZoneId", testAdEventSink.testEvents.first().zoneId)
    }

    @Test
    fun failed() {
        val testAdContent = AdContent.createAddToListContent(Ad("adContentId", "testZoneId", payload = Payload(testAddTolistItems)))
        testAppEventSink.testErrors = mutableSetOf()
        testAdContent.failed("adContentFail")
        AppEventClient.getInstance().onPublishEvents()
        assertEquals(EventStrings.ATL_ADDED_TO_LIST_FAILED, testAppEventSink.testErrors.first().code)
        assertEquals("adContentFail", testAppEventSink.testErrors.first().message)
    }

    @Test
    fun itemFailed() {
        val testAdContent = AdContent.createAddToListContent(Ad("adContentId", "testZoneId", payload = Payload(testAddTolistItems)))
        testAppEventSink.testErrors = mutableSetOf()
        testAdContent.itemFailed(testAddTolistItems.first(), "adContentFail")
        AppEventClient.getInstance().onPublishEvents()
        assertEquals(EventStrings.ATL_ADDED_TO_LIST_ITEM_FAILED, testAppEventSink.testErrors.first().code)
        assertEquals("adContentFail", testAppEventSink.testErrors.first().message)
    }

    @Test
    fun emptyPayload() {
        val testAdContent = AdContent.createAddToListContent(Ad("adContentId", "testZoneId"))
        testAppEventSink.testErrors = mutableSetOf()
        testAdContent.failed("adContentFail")
        AppEventClient.getInstance().onPublishEvents()
        assertTrue(testAppEventSink.testErrors.any { event -> event.code == EventStrings.AD_PAYLOAD_IS_EMPTY})
    }
}