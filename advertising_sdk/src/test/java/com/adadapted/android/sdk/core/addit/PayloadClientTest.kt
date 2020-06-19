package com.adadapted.android.sdk.core.addit

import androidx.test.platform.app.InstrumentationRegistry
import com.adadapted.android.sdk.config.EventStrings
import com.adadapted.android.sdk.core.atl.AddToListItem
import com.adadapted.android.sdk.core.concurrency.TransporterCoroutineScope
import com.adadapted.android.sdk.core.device.DeviceInfo
import com.adadapted.android.sdk.core.device.DeviceInfoClient
import com.adadapted.android.sdk.core.device.DeviceInfoClientTest
import com.adadapted.android.sdk.core.event.AppEventClient
import com.adadapted.android.sdk.core.event.TestAppEventSink
import com.adadapted.android.sdk.core.session.SessionClient
import com.adadapted.android.sdk.tools.TestTransporter
import com.nhaarman.mockitokotlin2.mock
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertNotNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class PayloadClientTest {

    private var testTransporter = TestCoroutineDispatcher()
    private val testTransporterScope: TransporterCoroutineScope = TestTransporter(testTransporter)
    private var testPayloadClient = PayloadClient
    private var testAppEventSink = TestAppEventSink()
    private var testPayloadAdapter = TestPayloadAdapter()

    @Before
    fun setup() {
        Dispatchers.setMain(testTransporter)
        DeviceInfoClient.createInstance(InstrumentationRegistry.getInstrumentation().targetContext,"", false, HashMap(), DeviceInfoClientTest.Companion::requestAdvertisingIdInfo, testTransporterScope)
        SessionClient.createInstance(mock(), mock())
        AppEventClient.createInstance(testAppEventSink, testTransporterScope)
        testPayloadClient.createInstance(testPayloadAdapter, AppEventClient.getInstance(), testTransporterScope)
    }

    @Test
    fun createInstance() {
        assertNotNull(testPayloadClient)
    }

    @Test
    fun pickupPayloads() {
        var testContent = listOf<AdditContent>()
        val callback = object: PayloadClient.Callback {
            override fun onPayloadAvailable(content: List<AdditContent>) {
                testContent = content
            }
        }

        assert(testContent.isEmpty())
        testPayloadClient.getInstance().pickupPayloads(callback)

        assert(testContent.isNotEmpty())
        assertEquals("testPayloadId", testContent.first().payloadId)
    }

    @Test
    fun deeplinkInProgressAndCompletes() {
        var testContent = listOf<AdditContent>()
        val callback = object: PayloadClient.Callback {
            override fun onPayloadAvailable(content: List<AdditContent>) {
                testContent = content
            }
        }

        assert(testContent.isEmpty())
        testPayloadClient.getInstance().deeplinkInProgress()
        testPayloadClient.getInstance().pickupPayloads(callback)

        assert(testContent.isEmpty())

        testPayloadClient.getInstance().deeplinkCompleted()
        testPayloadClient.getInstance().pickupPayloads(callback)

        assert(testContent.isNotEmpty())
        assertEquals("testPayloadId", testContent.first().payloadId)
    }

    @Test
    fun markContentAcknowledged() {
        val content = getTestAdditPayloadContent()
        testPayloadClient.getInstance().markContentAcknowledged(content)
        AppEventClient.getInstance().onPublishEvents()
        assertEquals(EventStrings.ADDIT_ADDED_TO_LIST, testAppEventSink.testEvents.first().name)
        assertEquals("testPayloadId", testAppEventSink.testEvents.first().params.getValue("payload_id"))
        assertEquals(AdditContent.AdditSources.PAYLOAD, testAppEventSink.testEvents.first().params.getValue("source"))
        assertEquals("delivered", testPayloadAdapter.publishedEvent.status)
    }

    @Test
    fun markNonPayloadContentAcknowledged() {
        val content = getTestAdditPayloadContent(isPayloadSource = false)
        testAppEventSink.testEvents = mutableSetOf()
        testPayloadClient.getInstance().markContentAcknowledged(content)
        AppEventClient.getInstance().onPublishEvents()
        assertEquals(EventStrings.ADDIT_ADDED_TO_LIST, testAppEventSink.testEvents.first().name)
        assertEquals("testPayloadId", testAppEventSink.testEvents.first().params.getValue("payload_id"))
        assertEquals("", testAppEventSink.testEvents.first().params.getValue("source"))
        assertEquals("", testPayloadAdapter.publishedEvent.status)
    }

    @Test
    fun markContentItemAcknowledged() {
        val content = getTestAdditPayloadContent()
        testPayloadClient.getInstance().markContentItemAcknowledged(content, getTestAddToListItem())
        AppEventClient.getInstance().onPublishEvents()
        assertEquals(EventStrings.ADDIT_ITEM_ADDED_TO_LIST, testAppEventSink.testEvents.first().name)
        assertEquals("testPayloadId", testAppEventSink.testEvents.first().params.getValue("payload_id"))
        assertEquals("testTitle", testAppEventSink.testEvents.first().params.getValue("item_name"))
        assertEquals(AdditContent.AdditSources.PAYLOAD, testAppEventSink.testEvents.first().params.getValue("source"))
    }

    @Test
    fun markContentDuplicate() {
        val content = getTestAdditPayloadContent()
        testAppEventSink.testEvents = mutableSetOf()
        testPayloadClient.getInstance().markContentDuplicate(content)
        AppEventClient.getInstance().onPublishEvents()
        assertEquals(EventStrings.ADDIT_DUPLICATE_PAYLOAD, testAppEventSink.testEvents.first().name)
        assertEquals("testPayloadId", testAppEventSink.testEvents.first().params.getValue("payload_id"))
        assertEquals("duplicate", testPayloadAdapter.publishedEvent.status)
    }

    @Test
    fun markNonPayloadContentDuplicate() {
        val content = getTestAdditPayloadContent(isPayloadSource = false)
        testAppEventSink.testEvents = mutableSetOf()
        testPayloadClient.getInstance().markContentDuplicate(content)
        AppEventClient.getInstance().onPublishEvents()
        assertEquals(EventStrings.ADDIT_DUPLICATE_PAYLOAD, testAppEventSink.testEvents.first().name)
        assertEquals("testPayloadId", testAppEventSink.testEvents.first().params.getValue("payload_id"))
        assertEquals("", testPayloadAdapter.publishedEvent.status)
    }

    @Test
    fun markContentFailed() {
        val content = getTestAdditPayloadContent()
        testAppEventSink.testErrors = mutableSetOf()
        testPayloadClient.getInstance().markContentFailed(content, "testFail")
        AppEventClient.getInstance().onPublishEvents()
        assertEquals(EventStrings.ADDIT_CONTENT_FAILED, testAppEventSink.testErrors.first().code)
        assertEquals("testFail", testAppEventSink.testErrors.first().message)
        assertEquals("rejected", testPayloadAdapter.publishedEvent.status)
    }

    @Test
    fun markNonPayloadContentFailed() {
        val content = getTestAdditPayloadContent(isPayloadSource = false)
        testAppEventSink.testErrors = mutableSetOf()
        testPayloadClient.getInstance().markContentFailed(content, "testFail")
        AppEventClient.getInstance().onPublishEvents()
        assertEquals(EventStrings.ADDIT_CONTENT_FAILED, testAppEventSink.testErrors.first().code)
        assertEquals("testFail", testAppEventSink.testErrors.first().message)
        assertEquals("", testPayloadAdapter.publishedEvent.status)
    }

    @Test
    fun markContentItemFailed() {
        val content = getTestAdditPayloadContent()
        testAppEventSink.testErrors = mutableSetOf()
        testPayloadClient.getInstance().markContentItemFailed(content, getTestAddToListItem(), "testItemFail")
        AppEventClient.getInstance().onPublishEvents()
        assertEquals(EventStrings.ADDIT_CONTENT_ITEM_FAILED, testAppEventSink.testErrors.first().code)
        assertEquals("testItemFail", testAppEventSink.testErrors.first().message)
    }

    companion object {
        fun getTestAdditPayloadContent(isPayloadSource: Boolean = true): AdditContent {
            return AdditContent("testPayloadId", "testMessage", "image", 0, "source", if (isPayloadSource) { AdditContent.AdditSources.PAYLOAD } else "", mutableListOf(getTestAddToListItem()))
        }

        fun getTestAddToListItem(): AddToListItem {
            return AddToListItem(
                    "testTrackId",
                    "testTitle",
                    "testBrand",
                    "testCategory",
                    "testUPC",
                    "testSKU",
                    "testDiscount",
                    "testImage")
        }
    }
}

class TestPayloadAdapter: PayloadAdapter {
    var publishedEvent = PayloadEvent("", "")

    override fun pickup(deviceInfo: DeviceInfo, callback: PayloadAdapter.Callback) {
        callback.onSuccess(listOf(PayloadClientTest.getTestAdditPayloadContent()))
    }

    override fun publishEvent(event: PayloadEvent) {
        publishedEvent = event
    }
}