package com.adadapted.android.sdk.core.atl

import com.adadapted.android.sdk.constants.EventStrings
import com.adadapted.android.sdk.core.concurrency.TransporterCoroutineScope
import com.adadapted.android.sdk.core.device.DeviceInfoClient
import com.adadapted.android.sdk.core.event.EventClient
import com.adadapted.android.sdk.core.session.SessionClient
import com.adadapted.android.sdk.tools.TestDeviceInfoExtractor
import com.adadapted.android.sdk.tools.TestEventAdapter
import com.adadapted.android.sdk.tools.TestTransporter
import com.nhaarman.mockitokotlin2.mock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.setMain
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PopupContentTest {
    private var testTransporter = UnconfinedTestDispatcher()
    private val testTransporterScope: TransporterCoroutineScope = TestTransporter(testTransporter)
    private var testAddTolistItems = listOf(AddToListItem("testTrackingId", "title", "brand", "cat", "upc", "sku", "discount", "image"))

    @Before
    fun setup() {
        Dispatchers.setMain(testTransporter)
        DeviceInfoClient.createInstance("", false, HashMap(), "", TestDeviceInfoExtractor(), testTransporterScope)
        SessionClient.onStart(mock())
        EventClient.createInstance(TestEventAdapter, testTransporterScope)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun createPopupContent() {
        val testPopupContent = PopupContent("testPayloadId", testAddTolistItems)
        assertEquals("testPayloadId", testPopupContent.payloadId)
    }

    @Test
    fun acknowledge() {
        val testPopupContent = PopupContent("testPayloadId", testAddTolistItems)
        TestEventAdapter.testSdkEvents = mutableListOf()
        testPopupContent.acknowledge()
        EventClient.onPublishEvents()
        assert(TestEventAdapter.testSdkEvents.any { event -> event.name == EventStrings.POPUP_ADDED_TO_LIST })
        var event = TestEventAdapter.testSdkEvents.first { event -> event.name == EventStrings.POPUP_ADDED_TO_LIST }
        assertEquals("testPayloadId", event.params.getValue("payload_id"))
    }

    @Test
    fun itemAcknowledge() {
        val testPopupContent = PopupContent("testPayloadId", testAddTolistItems)
        TestEventAdapter.testSdkEvents = mutableListOf()
        testPopupContent.itemAcknowledge(testPopupContent.getItems().first())
        EventClient.onPublishEvents()
        assert(TestEventAdapter.testSdkEvents.any { event -> event.name == EventStrings.POPUP_ADDED_TO_LIST })
        assert(TestEventAdapter.testSdkEvents.any { event -> event.name == EventStrings.POPUP_ITEM_ADDED_TO_LIST })

        assertEquals("testPayloadId", TestEventAdapter.testSdkEvents.last().params.getValue("payload_id"))
    }

    @Test
    fun failed() {
        val testPopupContent = PopupContent("testPayloadId", testAddTolistItems)
        TestEventAdapter.testSdkErrors = mutableListOf()
        testPopupContent.failed("popupFail")
        EventClient.onPublishEvents()
        assert(TestEventAdapter.testSdkErrors.any { event -> event.code == EventStrings.POPUP_CONTENT_FAILED })
        var event = TestEventAdapter.testSdkErrors.first { event -> event.code == EventStrings.POPUP_CONTENT_FAILED }
        assertEquals("popupFail", event.message)
    }

    @Test
    fun itemFailed() {
        val testPopupContent = PopupContent("testPayloadId", testAddTolistItems)
        TestEventAdapter.testSdkErrors = mutableListOf()
        testPopupContent.itemFailed(testAddTolistItems.first(), "popupItemFail")
        EventClient.onPublishEvents()
        assertEquals(EventStrings.POPUP_CONTENT_ITEM_FAILED, TestEventAdapter.testSdkErrors.first().code)
        assertEquals("popupItemFail", TestEventAdapter.testSdkErrors.first().message)
    }

    @Test
    fun popupContentGetSourceIsCorrect() {
        val testPopupContent = PopupContent("testPayloadId", testAddTolistItems)
        assertEquals(testPopupContent.getSource(), "in_app")
    }
}