package com.adadapted.android.sdk.core.zone

import android.os.Looper
import android.view.View
import androidx.test.platform.app.InstrumentationRegistry
import com.adadapted.android.sdk.constants.EventStrings
import com.adadapted.android.sdk.core.ad.Ad
import com.adadapted.android.sdk.core.ad.AdContent
import com.adadapted.android.sdk.core.ad.TestAdContentListener
import com.adadapted.android.sdk.core.common.DimensionConverter
import com.adadapted.android.sdk.core.event.TestAppEventSink
import com.adadapted.android.sdk.ext.models.Payload
import com.adadapted.android.sdk.tools.TestAdEventSink
import com.adadapted.android.sdk.tools.TestDeviceInfoExtractor
import com.adadapted.android.sdk.tools.TestTransporter
import com.adadapted.android.sdk.ui.messaging.AdContentPublisher
import com.adadapted.android.sdk.ui.view.AaZoneView
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import kotlin.collections.HashMap

@RunWith(RobolectricTestRunner::class)
class AaZoneViewTest {
    private var mockAdEventSink = mock<TestAdEventSink>()
    private var testContext = InstrumentationRegistry.getInstrumentation().targetContext
    private lateinit var testAaZoneView: AaZoneView
    private var testTransporter = TestCoroutineDispatcher()
    private val testTransporterScope: TransporterCoroutineScope = TestTransporter(testTransporter)
    private var testAppEventSink = TestAppEventSink()
    private var mockSession = Session("testId", true, true, 30, 1907245044, mutableMapOf())


    @Before
    fun setup() {
        whenever(mockAdEventSink.sendBatch(any(), any())).then { }

        Dispatchers.setMain(testTransporter)
        DeviceInfoClient.createInstance(testContext,"", false, HashMap(), "", TestDeviceInfoExtractor(), testTransporterScope)
        SessionClient.createInstance(mock(), mock())
        AdEventClient.createInstance(mockAdEventSink, testTransporterScope)
        AdEventClient.getInstance().onSessionAvailable(mockSession)
        AppEventClient.createInstance(testAppEventSink, testTransporterScope)
        DimensionConverter.createInstance(0f)
        AdContentPublisher.createInstance()
        testAaZoneView = AaZoneView(testContext)
    }

    @Test
    fun testStart() {
        val testListener = TestAaZoneViewListener()
        val testAd = (Ad("NewAdId"))
        testAaZoneView.init("TestZoneId")
        testAaZoneView.onStart(testListener)
        testAaZoneView.onAdAvailable(testAd)
        testAaZoneView.onAdLoaded(testAd)
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        assertEquals(testListener.adLoaded, true)
    }

    @Test
    fun testStartContentListener() {
        val testAdContentListener = TestAdContentListener()
        val testAd = (Ad("NewAdId"))
        testAaZoneView.init("TestZoneId")
        testAaZoneView.onStart(testAdContentListener)
        testAaZoneView.onAdAvailable(testAd)
        testAaZoneView.onAdLoaded(testAd)
        AdContentPublisher.getInstance().publishContent("TestZoneId", AdContent.createAddToListContent(Ad(payload = Payload(listOf(AddToListItem("trackId", "title", "brand", "cat", "upc", "sku", "disc", "image"))))))
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        assertEquals("TestZoneId", testAdContentListener.resultZoneId)
    }

    @Test
    fun testStartBothListeners() {
        val testListener = TestAaZoneViewListener()
        val testAdContentListener = TestAdContentListener()
        val testAd = (Ad("NewAdId"))
        testAaZoneView.init("TestZoneId")
        testAaZoneView.onStart(testListener, testAdContentListener)
        testAaZoneView.onAdAvailable(testAd)
        testAaZoneView.onAdLoaded(testAd)
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        assertEquals(testListener.adLoaded, true)
    }

    @Test
    fun testNoAdStart() {
        val testListener = TestAaZoneViewListener()
        testAaZoneView.init("TestZoneId")
        testAaZoneView.onStart(testListener)
        testAaZoneView.onNoAdAvailable()
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        assertEquals(testListener.adLoaded, false)
    }

    @Test
    fun testOnStop() {
        val testListener = TestAaZoneViewListener()
        testAaZoneView.init("TestZoneId")
        testAaZoneView.onStart(testListener)
        testAaZoneView.onAdsRefreshed(Zone("TestZoneId",ads = listOf(Ad("NewZoneAdId"))))
        testAaZoneView.onStop()
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        assertEquals(testListener.zoneHasAds, false)
    }

    @Test
    fun testOnStopWithContentListener() {
        val testListener = TestAaZoneViewListener()
        val testAdContentListener = TestAdContentListener()
        testAaZoneView.init("TestZoneId")
        testAaZoneView.onStart(testListener, testAdContentListener)
        testAaZoneView.onAdsRefreshed(Zone("TestZoneId",ads = listOf(Ad("NewZoneAdId"))))
        testAaZoneView.onStop(testAdContentListener)
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        assertEquals(testListener.zoneHasAds, false)
    }

    @Test
    fun testShutdown() {
        val testListener = TestAaZoneViewListener()
        testAaZoneView.init("TestZoneId")
        testAaZoneView.onStart(testListener)

        testAaZoneView.shutdown()
        testAaZoneView.onAdAvailable(Ad("NewAdId"))

        assertEquals(testListener.adLoaded, false)
    }

    @Test
    fun testOnZoneAvail() {
        val testListener = TestAaZoneViewListener()
        testAaZoneView.init("TestZoneId")
        testAaZoneView.onStart(testListener)
        testAaZoneView.onZoneAvailable(Zone("TestZoneId",ads = listOf(Ad("NewZoneAdId"))))
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        assertEquals(testListener.zoneHasAds, true)
    }

    @Test
    fun testOnAdsRefreshed() {
        val testListener = TestAaZoneViewListener()
        testAaZoneView.init("TestZoneId")
        testAaZoneView.onStart(testListener)
        testAaZoneView.onAdsRefreshed(Zone("TestZoneId",ads = listOf(Ad("NewZoneAdId"))))
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        assertEquals(testListener.zoneHasAds, true)
    }

    @Test
    fun testOnAdLoaded() {
        val testListener = TestAaZoneViewListener()
        testAaZoneView.init("TestZoneId")
        testAaZoneView.onStart(testListener)
        testAaZoneView.onAdLoaded(Ad("NewAdId"))
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        assertEquals(testListener.adLoaded, true)
    }

    @Test
    fun testOnAdFailed() {
        val testListener = TestAaZoneViewListener()
        testAaZoneView.init("TestZoneId")
        testAaZoneView.onStart(testListener)
        testAaZoneView.onAdLoadFailed()
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        assertEquals(testListener.adFailed, true)
    }

    @Test
    fun testOnBlankAdDisplayed() {
        val testListener = TestAaZoneViewListener()
        testAaZoneView.init("TestZoneId")
        testAaZoneView.onStart(testListener)
        testAaZoneView.onBlankLoaded()
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        assertEquals(testListener.adLoaded, false)
    }

    @Test
    fun onVisibilityChanged() {
        val testListener = TestAaZoneViewListener()
        testAaZoneView.init("TestZoneId")
        testAaZoneView.onStart(testListener)
        testAaZoneView.visibility = View.GONE
        testAaZoneView.visibility = View.VISIBLE
        testAaZoneView.onAdLoaded(Ad("NewAdId"))
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        assertEquals(testListener.adLoaded, true)
    }

    @Test
    fun testOnAdClicked() {
        val testListener = TestAaZoneViewListener()
        val testAd = Ad("NewAdId", actionType = "c")
        testAaZoneView.init("TestZoneId")
        testAaZoneView.onStart(testListener)
        testAaZoneView.onAdLoaded(testAd)
        testAaZoneView.onAdClicked(testAd)
        AppEventClient.getInstance().onPublishEvents()
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        assertEquals(testListener.adLoaded, true)
        assert(testAppEventSink.testEvents.any { event -> event.name == EventStrings.ATL_AD_CLICKED })
    }
}

class TestAaZoneViewListener: AaZoneView.Listener {
    var zoneHasAds = false
    var adLoaded = false
    var adFailed = false

    override fun onZoneHasAds(hasAds: Boolean) {
        zoneHasAds = hasAds
    }

    override fun onAdLoaded() {
        adLoaded = true
    }

    override fun onAdLoadFailed() {
        adFailed = true
    }
}