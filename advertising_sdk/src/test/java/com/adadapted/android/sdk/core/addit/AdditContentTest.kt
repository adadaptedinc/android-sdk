package com.adadapted.android.sdk.core.addit

import com.adadapted.android.sdk.constants.AddToListTypes
import com.adadapted.android.sdk.constants.EventStrings
import com.adadapted.android.sdk.core.atl.AddToListContent
import com.adadapted.android.sdk.core.atl.AddToListItem
import com.adadapted.android.sdk.core.atl.AdditContent
import com.adadapted.android.sdk.core.concurrency.TransporterCoroutineScope
import com.adadapted.android.sdk.core.device.DeviceInfoClient
import com.adadapted.android.sdk.core.event.EventClient
import com.adadapted.android.sdk.core.payload.PayloadClient
import com.adadapted.android.sdk.core.session.SessionClient
import com.adadapted.android.sdk.tools.MockData
import com.adadapted.android.sdk.tools.TestEventAdapter
import com.adadapted.android.sdk.tools.TestTransporter
import com.nhaarman.mockitokotlin2.mock
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import kotlin.test.AfterTest
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.util.LinkedList

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class AdditContentTest {
    private var testTransporter = UnconfinedTestDispatcher()
    private val testTransporterScope: TransporterCoroutineScope = TestTransporter(testTransporter)
    private var testPayloadAdapter = TestPayloadAdapter()

    @Before
    fun setup() {
        DeviceInfoClient.createInstance("", false, mock(), "", mock(), mock())
        SessionClient.createInstance(mock(), mock())
        EventClient.createInstance(TestEventAdapter, testTransporterScope)
        EventClient.onSessionAvailable(MockData.session)
        PayloadClient.createInstance(testPayloadAdapter, EventClient, testTransporterScope)
        TestEventAdapter.testAdEvents = mutableListOf()
        TestEventAdapter.testSdkErrors = mutableListOf()
    }

    @AfterTest
    fun cleanup() {
        TestEventAdapter.cleanupEvents()
    }

    @Test
    fun createGenericAdditContent() {
        val content = AdditContent(
            "payloadId",
            "message",
            "image",
            0,
            "source",
            "additSource", listOf(), PayloadClient)

        assertEquals("payloadId", content.payloadId)
        assertEquals("message", content.message)
        assertEquals("image", content.image)
        assertEquals("additSource", content.additSource)
    }


    @Test
    fun acknowledge() {
        val content = AdditContent(
            "payloadId",
            "message",
            "image",
            AddToListTypes.ADD_TO_LIST_ITEMS,
            AddToListContent.Sources.IN_APP,
            AddToListContent.Sources.IN_APP,
            LinkedList()
        )
        content.acknowledge()
        content.acknowledge()
        EventClient.onPublishEvents()
        assertEquals(EventStrings.ADDIT_ADDED_TO_LIST, TestEventAdapter.testSdkEvents.first().name)
        assertEquals(1, TestEventAdapter.testSdkEvents.count())
    }

    @Test
    fun itemAcknowledge() {
        val content = AdditContent(
            "payloadId",
            "message",
            "image",
            AddToListTypes.ADD_TO_LIST_ITEMS,
            AddToListContent.Sources.IN_APP,
            AddToListContent.Sources.IN_APP,
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
        EventClient.onPublishEvents()
        assert(TestEventAdapter.testSdkEvents.any { event -> event.name == EventStrings.ADDIT_ITEM_ADDED_TO_LIST })
    }

    @Test
    fun duplicate() {
        val content = AdditContent(
                "payloadId",
                "message",
                "image",
            AddToListTypes.ADD_TO_LIST_ITEMS,
            AddToListContent.Sources.IN_APP,
            AddToListContent.Sources.IN_APP,
                LinkedList()
        )
        content.duplicate()
        EventClient.onPublishEvents()
        assert(TestEventAdapter.testSdkEvents.any { event -> event.name == EventStrings.ADDIT_DUPLICATE_PAYLOAD })

        content.duplicate()
        assert(TestEventAdapter.testSdkEvents.any { event -> event.name == EventStrings.ADDIT_DUPLICATE_PAYLOAD })
    }

    @Test
    fun failed() {
        val content = AdditContent(
                "payloadId",
                "message",
                "image",
            AddToListTypes.ADD_TO_LIST_ITEMS,
            AddToListContent.Sources.IN_APP,
            AddToListContent.Sources.IN_APP,
                LinkedList()
        )
        content.failed("test failed message")
        EventClient.onPublishEvents()
        assert(TestEventAdapter.testSdkErrors.any { event -> event.code == EventStrings.ADDIT_CONTENT_FAILED })

        content.failed("test failed message")
        assert(TestEventAdapter.testSdkErrors.any { event -> event.code == EventStrings.ADDIT_CONTENT_FAILED })
    }

    @Test
    fun itemFailed() {
        val content = AdditContent(
                "payloadId",
                "message",
                "image",
            AddToListTypes.ADD_TO_LIST_ITEMS,
            AddToListContent.Sources.IN_APP,
            AdditContent.AdditSources.PAYLOAD,
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
        EventClient.onPublishEvents()
        assert(TestEventAdapter.testSdkErrors.any { event -> event.code == EventStrings.ADDIT_CONTENT_ITEM_FAILED })
        assert(TestEventAdapter.testSdkErrors.any { event -> event.code == EventStrings.ADDIT_CONTENT_FAILED })
    }

    @Test
    fun addItSourcesAreCorrect() {
        val addItContentSources = AdditContent.AdditSources
        val sourceOne = addItContentSources.DEEPLINK
        val sourceTwo = AddToListContent.Sources.IN_APP
        val sourceThree = addItContentSources.PAYLOAD

        assertEquals(sourceOne, "deeplink")
        assertEquals(sourceTwo, "in_app")
        assertEquals(sourceThree, "payload")
    }

    @Test
    fun contentTypesAreCorrect() {
        val contentTypeWrapper = AddToListTypes
        val atlItem = contentTypeWrapper.ADD_TO_LIST_ITEM
        val atlItems = contentTypeWrapper.ADD_TO_LIST_ITEMS

        assertEquals(atlItem, 2)
        assertEquals(atlItems, 1)
    }
}