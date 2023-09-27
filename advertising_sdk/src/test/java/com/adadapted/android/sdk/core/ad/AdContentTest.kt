package com.adadapted.android.sdk.core.ad

import com.adadapted.android.sdk.constants.EventStrings
import com.adadapted.android.sdk.core.atl.AddToListItem
import com.adadapted.android.sdk.core.concurrency.TransporterCoroutineScope
import com.adadapted.android.sdk.core.device.DeviceInfoClient
import com.adadapted.android.sdk.core.event.AdEventTypes
import com.adadapted.android.sdk.core.event.EventClient
import com.adadapted.android.sdk.core.payload.Payload
import com.adadapted.android.sdk.core.session.SessionClient
import com.adadapted.android.sdk.tools.MockData
import com.adadapted.android.sdk.tools.TestDeviceInfoExtractor
import com.adadapted.android.sdk.tools.TestEventAdapter
import com.adadapted.android.sdk.tools.TestTransporter
import com.nhaarman.mockitokotlin2.mock
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class AdContentTest {
    private var testTransporter = UnconfinedTestDispatcher()
    private val testTransporterScope: TransporterCoroutineScope = TestTransporter(testTransporter)
    private var testAddTolistItems = listOf(AddToListItem("testTrackingId", "title", "brand", "cat", "upc", "sku", "discount", "image"))

    @Before
    fun setup() {
        Dispatchers.setMain(testTransporter)
        DeviceInfoClient.createInstance("", false, HashMap(), "", TestDeviceInfoExtractor(), testTransporterScope)
        SessionClient.createInstance(mock(), mock())
        EventClient.createInstance(TestEventAdapter, testTransporterScope)
        EventClient.onSessionAvailable(MockData.session)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        TestEventAdapter.cleanupEvents()
    }

    @Test
    fun createAdContent() {
        val testAdContent = AdContent.createAddToListContent(Ad("adContentId", "testZoneId"))
        assertEquals("testZoneId", testAdContent.zoneId)
    }

    @Test
    fun acknowledge() {
        val testAdContent = AdContent.createAddToListContent(Ad("adContentId", "testZoneId"))
        TestEventAdapter.testAdEvents = mutableListOf()
        testAdContent.acknowledge()
        EventClient.onPublishEvents()
        assertEquals(AdEventTypes.INTERACTION, TestEventAdapter.testAdEvents.first().eventType)
        assertEquals("testZoneId", TestEventAdapter.testAdEvents.first().zoneId)
        assertEquals("adContentId", TestEventAdapter.testAdEvents.first().adId)
    }

    @Test
    fun itemAcknowledge() {
        val testAdContent = AdContent.createAddToListContent(Ad("adContentId", "testZoneId", payload = Payload(detailedListItems = testAddTolistItems)))
        TestEventAdapter.testAdEvents = mutableListOf()
        testAdContent.itemAcknowledge(testAdContent.getItems().first())
        EventClient.onPublishEvents()
        assert(TestEventAdapter.testAdEvents.any { event -> event.eventType == AdEventTypes.INTERACTION })
        assert(TestEventAdapter.testSdkEvents.any { event -> event.name == EventStrings.ATL_ITEM_ADDED_TO_LIST })
        assertEquals("adContentId", TestEventAdapter.testAdEvents.first().adId)
        assertEquals("testZoneId", TestEventAdapter.testAdEvents.first().zoneId)
    }

    @Test
    fun failed() {
        val testAdContent = AdContent.createAddToListContent(Ad("adContentId", "testZoneId", payload = Payload(detailedListItems = testAddTolistItems)))
        TestEventAdapter.testSdkErrors = mutableListOf()
        testAdContent.failed("adContentFail")
        EventClient.onPublishEvents()
        assertEquals(EventStrings.ATL_ADDED_TO_LIST_FAILED, TestEventAdapter.testSdkErrors.first().code)
        assertEquals("adContentFail", TestEventAdapter.testSdkErrors.first().message)
        testAdContent.failed("adContentFail")
    }

    @Test
    fun itemFailed() {
        val testAdContent = AdContent.createAddToListContent(Ad("adContentId", "testZoneId", payload = Payload(detailedListItems = testAddTolistItems)))
        TestEventAdapter.testSdkErrors = mutableListOf()
        testAdContent.itemFailed(testAddTolistItems.first(), "adContentFail")
        EventClient.onPublishEvents()
        assertEquals(EventStrings.ATL_ADDED_TO_LIST_ITEM_FAILED, TestEventAdapter.testSdkErrors.first().code)
        assertEquals("adContentFail", TestEventAdapter.testSdkErrors.first().message)
    }

    @Test
    fun emptyPayload() {
        val testAdContent = AdContent.createAddToListContent(Ad("adContentId", "testZoneId"))
        TestEventAdapter.testSdkErrors = mutableListOf()
        testAdContent.failed("adContentFail")
        EventClient.onPublishEvents()
        assertTrue(TestEventAdapter.testSdkErrors.any { event -> event.code == EventStrings.AD_PAYLOAD_IS_EMPTY})
    }

    @Test
    fun getSourceIsCorrect() {
        val testAdContent = AdContent.createAddToListContent(Ad("adContentId", "testZoneId"))
        val source = testAdContent.getSource()
        assertEquals("in_app", source)
    }

    @Test
    fun hasNoItemsIsCorrect() {
        val testAdContent = AdContent.createAddToListContent(Ad("adContentId", "testZoneId"))
        assertEquals(testAdContent.hasNoItems(), true)
    }
}