package com.adadapted.android.sdk.core.webview

import com.adadapted.android.sdk.constants.EventStrings
import com.adadapted.android.sdk.core.ad.Ad
import com.adadapted.android.sdk.core.concurrency.TransporterCoroutineScope
import com.adadapted.android.sdk.core.device.DeviceInfoClient
import com.adadapted.android.sdk.core.event.EventClient
import com.adadapted.android.sdk.core.session.SessionClient
import com.adadapted.android.sdk.core.view.JavascriptBridge
import com.adadapted.android.sdk.tools.MockData
import com.adadapted.android.sdk.tools.TestEventAdapter
import com.adadapted.android.sdk.tools.TestTransporter
import com.nhaarman.mockitokotlin2.mock
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PopupJavascriptBridgeTest {
    private var testTransporter = UnconfinedTestDispatcher()
    private val testTransporterScope: TransporterCoroutineScope = TestTransporter(testTransporter)
    private val testBridge = JavascriptBridge(Ad("bridgeAdId"))

    @Before
    fun setup() {
        DeviceInfoClient.createInstance("", false, mock(), "", mock(), mock())
        SessionClient.createInstance(mock(), mock())
        EventClient.createInstance(TestEventAdapter, testTransporterScope)
        EventClient.onSessionAvailable(MockData.session)
        TestEventAdapter.testSdkEvents = mutableListOf()
        TestEventAdapter.testSdkErrors = mutableListOf()
    }

    @Test
    fun deliverContentTest() {
        testBridge.deliverAdContent()
        EventClient.onPublishEvents()
        assertEquals(EventStrings.POPUP_CONTENT_CLICKED, TestEventAdapter.testSdkEvents.first().name)
    }

    @Test
    fun addItemToListTest() {
        testBridge.addItemToList("payloadId", "trackingId", "title", "brand", "category", "barCode", "retailerSku", "discount", "image")
        EventClient.onPublishEvents()
        assertEquals(EventStrings.POPUP_ATL_CLICKED, TestEventAdapter.testSdkEvents.first().name)
    }
}