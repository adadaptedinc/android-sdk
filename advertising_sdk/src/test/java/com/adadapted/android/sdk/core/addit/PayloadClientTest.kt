package com.adadapted.android.sdk.core.addit

import com.adadapted.android.sdk.constants.EventStrings
import com.adadapted.android.sdk.core.atl.AddToListItem
import com.adadapted.android.sdk.core.atl.AdditContent
import com.adadapted.android.sdk.core.concurrency.TransporterCoroutineScope
import com.adadapted.android.sdk.core.device.DeviceInfo
import com.adadapted.android.sdk.core.device.DeviceInfoClient
import com.adadapted.android.sdk.core.event.EventClient
import com.adadapted.android.sdk.core.payload.PayloadAdapter
import com.adadapted.android.sdk.core.payload.PayloadClient
import com.adadapted.android.sdk.core.payload.PayloadEvent
import com.adadapted.android.sdk.core.session.SessionClient
import com.adadapted.android.sdk.tools.MockData
import com.adadapted.android.sdk.tools.TestDeviceInfoExtractor
import com.adadapted.android.sdk.tools.TestEventAdapter
import com.adadapted.android.sdk.tools.TestTransporter
import com.nhaarman.mockitokotlin2.mock
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertNotNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.Before
import kotlin.test.AfterTest
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class PayloadClientTest {
    private var testTransporter = UnconfinedTestDispatcher()
    private val testTransporterScope: TransporterCoroutineScope = TestTransporter(testTransporter)
    private var testPayloadClient = PayloadClient
    private var testPayloadAdapter = TestPayloadAdapter()

    @Before
    fun setup() {
        Dispatchers.setMain(testTransporter)
        DeviceInfoClient.createInstance("", false, HashMap(), "", TestDeviceInfoExtractor(), testTransporterScope)
        SessionClient.createInstance(mock(), mock())
        EventClient.createInstance(TestEventAdapter, testTransporterScope)
        EventClient.onSessionAvailable(MockData.session)
        testPayloadClient.createInstance(testPayloadAdapter, EventClient, testTransporterScope)
    }

    @AfterTest
    fun cleanup() {
        TestEventAdapter.cleanupEvents()
    }

    @Test
    fun createInstance() {
        assertNotNull(testPayloadClient)
    }

    @Test
    fun pickupPayloads() {
        var testContent = listOf<AdditContent>()

        assert(testContent.isEmpty())
        testPayloadClient.pickupPayloads {
            testContent = it
        }

        assert(testContent.isNotEmpty())
        assertEquals("testPayloadId", testContent.first().payloadId)
    }

    @Test
    fun deeplinkInProgressAndCompletes() {
        var testContent = listOf<AdditContent>()

        assert(testContent.isEmpty())
        testPayloadClient.deeplinkInProgress()
        testPayloadClient.pickupPayloads{
            testContent = it
        }

        assert(testContent.isEmpty())

        testPayloadClient.deeplinkCompleted()
        testPayloadClient.pickupPayloads{
            testContent = it
        }

        assert(testContent.isNotEmpty())
        assertEquals("testPayloadId", testContent.first().payloadId)
    }

    @Test
    fun markContentAcknowledged() {
        val content = getTestAdditPayloadContent()
        testPayloadClient.markContentAcknowledged(content)
        EventClient.onPublishEvents()
        assertEquals(EventStrings.ADDIT_ADDED_TO_LIST, TestEventAdapter.testSdkEvents.first().name)
        assertEquals("testPayloadId", TestEventAdapter.testSdkEvents.first().params.getValue("payload_id"))
        assertEquals(AdditContent.AdditSources.PAYLOAD, TestEventAdapter.testSdkEvents.first().params.getValue("source"))
        assertEquals("delivered", testPayloadAdapter.publishedEvent.status)
    }

    @Test
    fun markNonPayloadContentAcknowledged() {
        val content = getTestAdditPayloadContent(isPayloadSource = false)
        TestEventAdapter.testSdkEvents = mutableListOf()
        testPayloadClient.markContentAcknowledged(content)
        EventClient.onPublishEvents()
        assertEquals(EventStrings.ADDIT_ADDED_TO_LIST, TestEventAdapter.testSdkEvents.first().name)
        assertEquals("testPayloadId", TestEventAdapter.testSdkEvents.first().params.getValue("payload_id"))
        assertEquals("", TestEventAdapter.testSdkEvents.first().params.getValue("source"))
        assertEquals("", testPayloadAdapter.publishedEvent.status)
    }

    @Test
    fun markContentItemAcknowledged() {
        val content = getTestAdditPayloadContent()
        testPayloadClient.markContentItemAcknowledged(content, getTestAddToListItem())
        EventClient.onPublishEvents()
        assertEquals(EventStrings.ADDIT_ITEM_ADDED_TO_LIST, TestEventAdapter.testSdkEvents.first().name)
        assertEquals("testPayloadId", TestEventAdapter.testSdkEvents.first().params.getValue("payload_id"))
        assertEquals("testTitle", TestEventAdapter.testSdkEvents.first().params.getValue("item_name"))
        assertEquals(AdditContent.AdditSources.PAYLOAD, TestEventAdapter.testSdkEvents.first().params.getValue("source"))
    }

    @Test
    fun markContentDuplicate() {
        val content = getTestAdditPayloadContent()
        TestEventAdapter.testSdkEvents = mutableListOf()
        testPayloadClient.markContentDuplicate(content)
        EventClient.onPublishEvents()
        assertTrue {
            TestEventAdapter.testSdkEvents.any { e -> e.name ==  EventStrings.ADDIT_DUPLICATE_PAYLOAD }
            TestEventAdapter.testSdkEvents.first { e -> e.name == EventStrings.ADDIT_DUPLICATE_PAYLOAD }
                .params.getValue("payload_id") == "testPayloadId"
        }
        assertEquals("duplicate", testPayloadAdapter.publishedEvent.status)
    }

    @Test
    fun markNonPayloadContentDuplicate() {
        val content = getTestAdditPayloadContent(isPayloadSource = false)
        TestEventAdapter.testSdkEvents = mutableListOf()
        testPayloadClient.markContentDuplicate(content)
        EventClient.onPublishEvents()

        assertTrue {
            TestEventAdapter.testSdkEvents.any { e -> e.name == EventStrings.ADDIT_DUPLICATE_PAYLOAD }
            TestEventAdapter.testSdkEvents.first { e -> e.name == EventStrings.ADDIT_DUPLICATE_PAYLOAD }
                .params.getValue("payload_id") == "testPayloadId"
        }
        assertEquals("", testPayloadAdapter.publishedEvent.status)
    }

    @Test
    fun markContentFailed() {
        val content = getTestAdditPayloadContent()
        TestEventAdapter.testSdkErrors = mutableListOf()
        testPayloadClient.markContentFailed(content, "testFail")
        EventClient.onPublishEvents()
        assertTrue {
            TestEventAdapter.testSdkErrors.any { e -> e.code ==  EventStrings.ADDIT_CONTENT_FAILED }
            TestEventAdapter.testSdkErrors.any { e -> e.message == "testFail"}
        }
        assertEquals("rejected", testPayloadAdapter.publishedEvent.status)
    }

    @Test
    fun markNonPayloadContentFailed() {
        val content = getTestAdditPayloadContent(isPayloadSource = false)
        TestEventAdapter.testSdkErrors = mutableListOf()
        testPayloadClient.markContentFailed(content, "testFail")
        EventClient.onPublishEvents()
        assertTrue {
            TestEventAdapter.testSdkErrors.any { e -> e.code == EventStrings.ADDIT_CONTENT_FAILED }
            TestEventAdapter.testSdkErrors.any { e -> e.message == "testFail"}
        }
        assertEquals("", testPayloadAdapter.publishedEvent.status)
    }

    @Test
    fun markContentItemFailed() {
        val content = getTestAdditPayloadContent()
        TestEventAdapter.testSdkErrors = mutableListOf()
        testPayloadClient.markContentItemFailed(content, getTestAddToListItem(), "testItemFail")
        EventClient.onPublishEvents()
        assertTrue {
            TestEventAdapter.testSdkErrors.any { e -> e.code == EventStrings.ADDIT_CONTENT_ITEM_FAILED }
            TestEventAdapter.testSdkErrors.any { e -> e.message == "testItemFail" }
        }
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
    override suspend fun pickup(deviceInfo: DeviceInfo, callback: (content: List<AdditContent>) -> Unit) {
        callback(listOf(PayloadClientTest.getTestAdditPayloadContent()))
    }

    override suspend fun publishEvent(deviceInfo: DeviceInfo, event: PayloadEvent) {
        publishedEvent = event
    }
}