package com.adadapted.android.sdk.core.ad

import com.adadapted.android.sdk.core.atl.AddToListContent
import com.adadapted.android.sdk.core.atl.AddToListItem
import com.adadapted.android.sdk.core.concurrency.TransporterCoroutineScope
import com.adadapted.android.sdk.core.device.DeviceInfoClient
import com.adadapted.android.sdk.core.event.EventClient
import com.adadapted.android.sdk.core.payload.Payload
import com.adadapted.android.sdk.core.session.SessionClient
import com.adadapted.android.sdk.tools.MockData
import com.adadapted.android.sdk.tools.TestDeviceInfoExtractor
import com.adadapted.android.sdk.tools.TestEventAdapter
import com.adadapted.android.sdk.tools.TestTransporter
import com.nhaarman.mockitokotlin2.mock
import junit.framework.TestCase.assertNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AdContentPublisherTest {

    private var testTransporter = UnconfinedTestDispatcher()
    private val testTransporterScope: TransporterCoroutineScope = TestTransporter(testTransporter)

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        Dispatchers.setMain(testTransporter)
        DeviceInfoClient.createInstance("", false, HashMap(), "", TestDeviceInfoExtractor(), testTransporterScope)
        SessionClient.createInstance(mock(), mock())
        EventClient.createInstance(TestEventAdapter, testTransporterScope)
        EventClient.onSessionAvailable(MockData.session)
    }

    @Test
    fun listenerIsAddedAndPublished() {
        val testListener = TestAdContentListener()
        AdContentPublisher.addListener(testListener)
        AdContentPublisher.publishContent(
                "testZoneId",
                AdContent.createAddToListContent(Ad(
                        "adId",
                        "adZoneId",
                        payload = Payload(detailedListItems = listOf(
                                AddToListItem("track", "title", "brand", "cat", "upc", "sku", "discount", "image")
                        ))
                )))
        assertEquals("testZoneId", testListener.resultZoneId)
        assertEquals("adZoneId", (testListener.resultContent as AdContent).zoneId)
    }

    @Test
    fun listenerIsRemovedAndPublished() {
        val testListener = TestAdContentListener()
        AdContentPublisher.addListener(testListener)
        AdContentPublisher.removeListener(testListener)
        AdContentPublisher.publishContent(
                "testZoneId",
                AdContent.createAddToListContent(Ad(
                        "adId",
                        "adZoneId",
                        payload = Payload(detailedListItems = listOf(
                                AddToListItem("track", "title", "brand", "cat", "upc", "sku", "discount", "image"))))))
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