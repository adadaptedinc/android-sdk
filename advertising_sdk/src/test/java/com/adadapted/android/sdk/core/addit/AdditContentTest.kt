package com.adadapted.android.sdk.core.addit

import com.adadapted.android.sdk.config.EventStrings
import com.adadapted.android.sdk.core.atl.AddToListContent
import com.adadapted.android.sdk.core.atl.AddToListItem
import com.adadapted.android.sdk.core.concurrency.TransporterCoroutineScope
import com.adadapted.android.sdk.core.device.DeviceInfoClient
import com.adadapted.android.sdk.core.event.AppEventClient
import com.adadapted.android.sdk.core.event.TestAppEventSink
import com.adadapted.android.sdk.core.session.SessionClient
import com.adadapted.android.sdk.tools.TestTransporter
import com.nhaarman.mockitokotlin2.mock
import kotlinx.coroutines.test.TestCoroutineDispatcher
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.util.LinkedList

@RunWith(RobolectricTestRunner::class)
class AdditContentTest {
    private var testAppEventSink = TestAppEventSink()
    private var testTransporter = TestCoroutineDispatcher()
    private val testTransporterScope: TransporterCoroutineScope = TestTransporter(testTransporter)
    private var testPayloadAdapter = TestPayloadAdapter()

    @Before
    fun setup() {
        DeviceInfoClient.createInstance(mock(), "", false, mock(), mock(), mock())
        SessionClient.createInstance(mock(), mock())
        AppEventClient.createInstance(testAppEventSink, testTransporterScope)
        PayloadClient.createInstance(testPayloadAdapter, AppEventClient.getInstance(), testTransporterScope)
        testAppEventSink.testEvents.clear()
        testAppEventSink.testErrors.clear()
    }

    @Test
    fun createDeeplinkContent() {
        val content = AdditContent.createDeeplinkContent(
                "payloadId",
                "message",
                "image",
                ContentTypes.ADD_TO_LIST_ITEMS,
                LinkedList()
        )
        assertEquals("payloadId", content.payloadId)
        assertEquals("message", content.message)
        assertEquals("image", content.image)
        assertEquals(AdditContent.AdditSources.DEEPLINK, content.additSource)
        assertEquals(AddToListContent.Sources.OUT_OF_APP, content.getSource())
        assertEquals(0, content.getItems().size.toLong())
    }

    @Test
    fun createInAppContent() {
        val content = AdditContent.createInAppContent(
                "payloadId",
                "message",
                "image",
                ContentTypes.ADD_TO_LIST_ITEMS,
                LinkedList()
        )
        assertEquals("payloadId", content.payloadId)
        assertEquals("message", content.message)
        assertEquals("image", content.image)
        assertEquals(AdditContent.AdditSources.IN_APP, content.additSource)
        assertEquals(AddToListContent.Sources.IN_APP, content.getSource())
        assertEquals(0, content.getItems().size.toLong())
    }

    @Test
    fun createPayloadContent() {
        val content = AdditContent.createPayloadContent(
                "payloadId",
                "message",
                "image",
                ContentTypes.ADD_TO_LIST_ITEMS,
                LinkedList()
        )
        assertEquals("payloadId", content.payloadId)
        assertEquals("message", content.message)
        assertEquals("image", content.image)
        assertEquals(AdditContent.AdditSources.PAYLOAD, content.additSource)
        assertEquals(AddToListContent.Sources.OUT_OF_APP, content.getSource())
        assertEquals(0, content.getItems().size.toLong())
    }

    @Test
    fun acknowledge() {
        val content = AdditContent(
                "payloadId",
                "message",
                "image",
                ContentTypes.ADD_TO_LIST_ITEMS,
                AddToListContent.Sources.IN_APP,
                AdditContent.AdditSources.IN_APP,
                LinkedList()
        )
        content.acknowledge()
        content.acknowledge()
        AppEventClient.getInstance().onPublishEvents()
        assertEquals(EventStrings.ADDIT_ADDED_TO_LIST, testAppEventSink.testEvents.first().name)
        assertEquals(1, testAppEventSink.testEvents.count())
    }

    @Test
    fun itemAcknowledge() {
        val content = AdditContent(
                "payloadId",
                "message",
                "image",
                ContentTypes.ADD_TO_LIST_ITEMS,
                AddToListContent.Sources.IN_APP,
                AdditContent.AdditSources.IN_APP,
                LinkedList()
        )

        val addToListItem = AddToListItem(
                "trackingId",
                "title",
                "brand",
                "category",
                "productUpc",
                "retailerSku",
                "discount",
                "productImage"
        )

        content.itemAcknowledge(addToListItem)
        AppEventClient.getInstance().onPublishEvents()
        assert(testAppEventSink.testEvents.any { event -> event.name == EventStrings.ADDIT_ITEM_ADDED_TO_LIST })
    }

    @Test
    fun duplicate() {
        val content = AdditContent(
                "payloadId",
                "message",
                "image",
                ContentTypes.ADD_TO_LIST_ITEMS,
                AddToListContent.Sources.IN_APP,
                AdditContent.AdditSources.IN_APP,
                LinkedList()
        )
        content.duplicate()
        AppEventClient.getInstance().onPublishEvents()
        assert(testAppEventSink.testEvents.any { event -> event.name == EventStrings.ADDIT_DUPLICATE_PAYLOAD })
    }

    @Test
    fun failed() {
        val content = AdditContent(
                "payloadId",
                "message",
                "image",
                ContentTypes.ADD_TO_LIST_ITEMS,
                AddToListContent.Sources.IN_APP,
                AdditContent.AdditSources.IN_APP,
                LinkedList()
        )
        content.failed("test failed message")
        AppEventClient.getInstance().onPublishEvents()
        assert(testAppEventSink.testErrors.any { event -> event.code == EventStrings.ADDIT_CONTENT_FAILED })
    }

    @Test
    fun itemFailed() {
        val content = AdditContent(
                "payloadId",
                "message",
                "image",
                ContentTypes.ADD_TO_LIST_ITEMS,
                AddToListContent.Sources.IN_APP,
                AdditContent.AdditSources.IN_APP,
                LinkedList()
        )
        content.itemFailed(AddToListItem(
                "trackingId",
                "title",
                "brand",
                "category",
                "productUpc",
                "retailerSku",
                "discount",
                "productImage"
        ), "test failed message")
        AppEventClient.getInstance().onPublishEvents()
        assert(testAppEventSink.testErrors.any { event -> event.code == EventStrings.ADDIT_CONTENT_ITEM_FAILED })
    }
}