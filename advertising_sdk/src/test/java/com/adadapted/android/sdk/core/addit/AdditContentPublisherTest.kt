package com.adadapted.android.sdk.core.addit

import android.os.Looper
import androidx.test.platform.app.InstrumentationRegistry
import com.adadapted.android.sdk.config.EventStrings
import com.adadapted.android.sdk.core.ad.Ad
import com.adadapted.android.sdk.core.ad.AdContent
import com.adadapted.android.sdk.core.ad.AdEventClient
import com.adadapted.android.sdk.core.atl.AddToListContent
import com.adadapted.android.sdk.core.atl.AddToListItem
import com.adadapted.android.sdk.core.atl.PopupContent
import com.adadapted.android.sdk.core.concurrency.TransporterCoroutineScope
import com.adadapted.android.sdk.core.device.DeviceInfo
import com.adadapted.android.sdk.core.device.DeviceInfoClient
import com.adadapted.android.sdk.core.event.AppEventClient
import com.adadapted.android.sdk.core.event.TestAppEventSink
import com.adadapted.android.sdk.core.session.Session
import com.adadapted.android.sdk.core.session.SessionClient
import com.adadapted.android.sdk.tools.TestAdEventSink
import com.adadapted.android.sdk.tools.TestDeviceInfoExtractor
import com.adadapted.android.sdk.tools.TestTransporter
import com.adadapted.android.sdk.ui.messaging.AaSdkAdditContentListener
import com.adadapted.android.sdk.ui.messaging.AdditContentPublisher
import com.nhaarman.mockitokotlin2.mock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import java.util.Date
import kotlin.collections.HashMap

@RunWith(RobolectricTestRunner::class)
class AdditContentPublisherTest {
    private var mockAdEventSink = mock<TestAdEventSink>()
    private var testContext = InstrumentationRegistry.getInstrumentation().targetContext
    private var testTransporter = TestCoroutineDispatcher()
    private val testTransporterScope: TransporterCoroutineScope = TestTransporter(testTransporter)
    private var testAppEventSink = TestAppEventSink()
    private var mockSession = Session(DeviceInfo(), "testId", true, true, 30, Date(1907245044), mutableMapOf())
    private lateinit var testAdditContent: AdditContent
    private lateinit var testAdditContentDupe: AdditContent

    @Before
    fun setup() {
        Dispatchers.setMain(testTransporter)
        DeviceInfoClient.createInstance(testContext,"", false, HashMap(), TestDeviceInfoExtractor(), testTransporterScope)
        SessionClient.createInstance(mock(), mock())
        AdEventClient.createInstance(mockAdEventSink, testTransporterScope)
        AdEventClient.getInstance().onSessionAvailable(mockSession)
        AppEventClient.createInstance(testAppEventSink, testTransporterScope)
        PayloadClient.createInstance(mock(), AppEventClient.getInstance(), testTransporterScope)
        testAdditContent = AdditContent("payloadId", "msg", "image", 1, "src", "additSrc", listOf(AddToListItem("trackId", "title", "brand", "cat", "upc", "sku", "disc", "img")))
        testAdditContentDupe = AdditContent("dupePayloadId", "msg", "image", 1, "src", "additSrc", listOf(AddToListItem("trackId", "title", "brand", "cat", "upc", "sku", "disc", "img")))
    }

    @Test
    fun addListenerAndPublishAdditContentNoListenerOrItems() {
        AdditContentPublisher.getInstance().publishAdditContent(AdditContent("payloadId", "msg", "image", 1, "src", "additSrc", listOf()))
        AppEventClient.getInstance().onPublishEvents()
        assertEquals(EventStrings.ADDIT_PAYLOAD_IS_EMPTY, testAppEventSink.testErrors.first().code)
    }

    @Test
    fun addListenerAndPublishPopupContentNoListenerOrItems() {
        AdditContentPublisher.getInstance().publishPopupContent(PopupContent.createPopupContent("payloadId", listOf()))
        AppEventClient.getInstance().onPublishEvents()
        assert(testAppEventSink.testErrors.isEmpty())
    }

    @Test
    fun addListenerAndPublishAdNoListenerOrItems() {
        AdditContentPublisher.getInstance().publishAdContent(AdContent.createAddToListContent(Ad("adId", "adZoneId", payload = listOf())))
        AppEventClient.getInstance().onPublishEvents()
        assertEquals(EventStrings.AD_PAYLOAD_IS_EMPTY, testAppEventSink.testErrors.first().code)
    }

    @Test
    fun addListenerAndPublishAdditContentNoListener() {
        AdditContentPublisher.getInstance().publishAdditContent(testAdditContent)
        AppEventClient.getInstance().onPublishEvents()
        assertEquals(EventStrings.NO_ADDIT_CONTENT_LISTENER, testAppEventSink.testErrors.first().code)
    }

    @Test
    fun addListenerAndPublishPopupContentNoListener() {
        AdditContentPublisher.getInstance().publishPopupContent(PopupContent.createPopupContent("payloadId", listOf(AddToListItem("trackId", "title", "brand", "cat", "upc", "sku", "disc", "img"))))
        AppEventClient.getInstance().onPublishEvents()
        assertEquals(EventStrings.NO_ADDIT_CONTENT_LISTENER, testAppEventSink.testErrors.first().code)
    }

    @Test
    fun addListenerAndPublishAdContentNoListener() {
        AdditContentPublisher.getInstance().publishAdContent(AdContent.createAddToListContent(Ad(
                "adId",
                "adZoneId",
                payload = listOf(
                        AddToListItem("track", "title", "brand", "cat", "upc", "sku", "discount", "image")))))

        AppEventClient.getInstance().onPublishEvents()
        assertEquals(EventStrings.NO_ADDIT_CONTENT_LISTENER, testAppEventSink.testErrors.first().code)
    }

    @Test
    fun addListenerAndPublishAdditContent() {
        val testListener = TestAaSdkAdditContentListener()
        AdditContentPublisher.getInstance().addListener(testListener)
        AdditContentPublisher.getInstance().publishAdditContent(testAdditContent)
        Shadows.shadowOf(Looper.getMainLooper()).idle()
        assertEquals("payloadId", (testListener.resultContent as AdditContent).payloadId)
    }

    @Test
    fun addListenerAndPublishPopupContent() {
        val testListener = TestAaSdkAdditContentListener()
        AdditContentPublisher.getInstance().addListener(testListener)
        AdditContentPublisher.getInstance().publishPopupContent(PopupContent.createPopupContent("payloadId", listOf(AddToListItem("trackId", "title", "brand", "cat", "upc", "sku", "disc", "img"))))
        Shadows.shadowOf(Looper.getMainLooper()).idle()
        assertEquals("payloadId", (testListener.resultContent as PopupContent).payloadId)
    }

    @Test
    fun addListenerAndPublishAdContent() {
        val testListener = TestAaSdkAdditContentListener()
        AdditContentPublisher.getInstance().addListener(testListener)
        AdditContentPublisher.getInstance().publishAdContent(AdContent.createAddToListContent(Ad(
                "adId",
                "adZoneId",
                payload = listOf(
                        AddToListItem("track", "title", "brand", "cat", "upc", "sku", "discount", "image")))))
        Shadows.shadowOf(Looper.getMainLooper()).idle()
        assertEquals("adZoneId", (testListener.resultContent as AdContent).zoneId)
    }

    @Test
    fun addListenerAndPublishAdditContentDuplicate() {
        val testListener = TestAaSdkAdditContentListener()
        AdditContentPublisher.getInstance().addListener(testListener)
        AdditContentPublisher.getInstance().publishAdditContent(testAdditContentDupe)
        AdditContentPublisher.getInstance().publishAdditContent(testAdditContentDupe)
        Shadows.shadowOf(Looper.getMainLooper()).idle()
        assertEquals("dupePayloadId", (testListener.resultContent as AdditContent).payloadId)
        AppEventClient.getInstance().onPublishEvents()
        assertEquals(EventStrings.ADDIT_DUPLICATE_PAYLOAD, testAppEventSink.testEvents.first().name)
    }
}

class TestAaSdkAdditContentListener: AaSdkAdditContentListener {
    var resultContent: AddToListContent? = null

    override fun onContentAvailable(content: AddToListContent) {
        resultContent = content
    }
}