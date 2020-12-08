package com.adadapted.android.sdk.core.atl

import com.adadapted.android.sdk.config.EventStrings
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
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.test.resetMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class PopupContentTest {
    private var testTransporter = TestCoroutineDispatcher()
    private val testTransporterScope: TransporterCoroutineScope = TestTransporter(testTransporter)
    private var testAppEventSink = TestAppEventSink()
    private var testAddTolistItems = listOf(AddToListItem("testTrackingId", "title", "brand", "cat", "upc", "sku", "discount", "image"))

    @Before
    fun setup() {
        Dispatchers.setMain(testTransporter)
        DeviceInfoClient.createInstance(mock(),"", false, HashMap(), TestDeviceInfoExtractor(), testTransporterScope)
        SessionClient.createInstance(mock(), mock())
        AppEventClient.createInstance(testAppEventSink, testTransporterScope)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testTransporter.cleanupTestCoroutines()
    }

    @Test
    fun createPopupContent() {
        val testPopupContent = PopupContent("testPayloadId", testAddTolistItems)
        assertEquals("testPayloadId", testPopupContent.payloadId)
    }

    @Test
    fun acknowledge() {
        val testPopupContent = PopupContent("testPayloadId", testAddTolistItems)
        testAppEventSink.testEvents = mutableSetOf()
        testPopupContent.acknowledge()
        AppEventClient.getInstance().onPublishEvents()
        assertEquals(EventStrings.POPUP_ADDED_TO_LIST, testAppEventSink.testEvents.first().name)
        assertEquals("testPayloadId", testAppEventSink.testEvents.first().params.getValue("payload_id"))
    }

    @Test
    fun itemAcknowledge() {
        val testPopupContent = PopupContent("testPayloadId", testAddTolistItems)
        testAppEventSink.testEvents = mutableSetOf()
        testPopupContent.itemAcknowledge(testPopupContent.getItems().first())
        AppEventClient.getInstance().onPublishEvents()
        assert(testAppEventSink.testEvents.count() == 2)
        assert(testAppEventSink.testEvents.any { event -> event.name == EventStrings.POPUP_ADDED_TO_LIST })
        assert(testAppEventSink.testEvents.any { event -> event.name == EventStrings.POPUP_ITEM_ADDED_TO_LIST })
        assertEquals("testPayloadId", testAppEventSink.testEvents.first().params.getValue("payload_id"))
        assertEquals("testPayloadId", testAppEventSink.testEvents.last().params.getValue("payload_id"))
    }

    @Test
    fun failed() {
        val testPopupContent = PopupContent("testPayloadId", testAddTolistItems)
        testAppEventSink.testErrors = mutableSetOf()
        testPopupContent.failed("popupFail")
        AppEventClient.getInstance().onPublishEvents()
        assertEquals(EventStrings.POPUP_CONTENT_FAILED, testAppEventSink.testErrors.first().code)
        assertEquals("popupFail", testAppEventSink.testErrors.first().message)
    }

    @Test
    fun itemFailed() {
        val testPopupContent = PopupContent("testPayloadId", testAddTolistItems)
        testAppEventSink.testErrors = mutableSetOf()
        testPopupContent.itemFailed(testAddTolistItems.first(), "popupItemFail")
        AppEventClient.getInstance().onPublishEvents()
        assertEquals(EventStrings.POPUP_CONTENT_ITEM_FAILED, testAppEventSink.testErrors.first().code)
        assertEquals("popupItemFail", testAppEventSink.testErrors.first().message)
    }
}