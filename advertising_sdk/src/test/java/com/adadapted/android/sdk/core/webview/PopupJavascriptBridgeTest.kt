package com.adadapted.android.sdk.core.webview

import com.adadapted.android.sdk.config.EventStrings
import com.adadapted.android.sdk.core.ad.Ad
import com.adadapted.android.sdk.core.ad.AdEventClient
import com.adadapted.android.sdk.core.concurrency.TransporterCoroutineScope
import com.adadapted.android.sdk.core.device.DeviceInfoClient
import com.adadapted.android.sdk.core.event.AppEventClient
import com.adadapted.android.sdk.core.event.TestAppEventSink
import com.adadapted.android.sdk.core.session.SessionClient
import com.adadapted.android.sdk.tools.TestTransporter
import com.adadapted.android.sdk.ui.activity.PopupJavascriptBridge
import com.nhaarman.mockitokotlin2.mock
import kotlinx.coroutines.test.TestCoroutineDispatcher
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class PopupJavascriptBridgeTest {
    private var testAppEventSink = TestAppEventSink()
    private var testTransporter = TestCoroutineDispatcher()
    private val testTransporterScope: TransporterCoroutineScope = TestTransporter(testTransporter)
    private val testBridge = PopupJavascriptBridge(Ad("bridgeAdId"))

    @Before
    fun setup() {
        DeviceInfoClient.createInstance(mock(), "", false, mock(), "", mock(), mock())
        SessionClient.createInstance(mock(), mock())
        AppEventClient.createInstance(testAppEventSink, testTransporterScope)
        AdEventClient.createInstance(mock(), mock())
        testAppEventSink.testEvents.clear()
        testAppEventSink.testErrors.clear()
    }

    @Test
    fun deliverContentTest() {
        testBridge.deliverAdContent()
        AppEventClient.getInstance().onPublishEvents()
        assertEquals(EventStrings.POPUP_CONTENT_CLICKED, testAppEventSink.testEvents.first().name)
    }

    @Test
    fun addItemToListTest() {
        testBridge.addItemToList("payloadId", "trackingId", "title", "brand", "category", "barCode", "retailerSku", "discount", "image")
        AppEventClient.getInstance().onPublishEvents()
        assertEquals(EventStrings.POPUP_ATL_CLICKED, testAppEventSink.testEvents.first().name)
    }
}