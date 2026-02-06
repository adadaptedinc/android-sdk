package com.adadapted.android.sdk.core.zone

import android.os.Looper
import android.util.DisplayMetrics
import android.view.View
import androidx.test.platform.app.InstrumentationRegistry
import com.adadapted.android.sdk.constants.EventStrings
import com.adadapted.android.sdk.core.ad.Ad
import com.adadapted.android.sdk.core.ad.AdContent
import com.adadapted.android.sdk.core.ad.AdContentPublisher
import com.adadapted.android.sdk.core.ad.TestAdContentListener
import com.adadapted.android.sdk.core.atl.AddToListItem
import com.adadapted.android.sdk.core.concurrency.TransporterCoroutineScope
import com.adadapted.android.sdk.core.device.DeviceInfoClient
import com.adadapted.android.sdk.core.event.EventClient
import com.adadapted.android.sdk.core.payload.Payload
import com.adadapted.android.sdk.core.session.SessionClient
import com.adadapted.android.sdk.core.view.AaZoneView
import com.adadapted.android.sdk.core.view.DimensionConverter
import com.adadapted.android.sdk.core.view.Zone
import com.adadapted.android.sdk.tools.MockData
import com.adadapted.android.sdk.tools.TestDeviceInfoExtractor
import com.adadapted.android.sdk.tools.TestEventAdapter
import com.adadapted.android.sdk.tools.TestTransporter
import com.nhaarman.mockitokotlin2.mock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.annotation.Config
import kotlin.intArrayOf

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33]) //temp until java21
class AaZoneViewTest {
    private var testContext = InstrumentationRegistry.getInstrumentation().targetContext
    private lateinit var testAaZoneView: AaZoneView
    private var testTransporter = UnconfinedTestDispatcher()
    private val testTransporterScope: TransporterCoroutineScope = TestTransporter(testTransporter)

    @Before
    fun setup() {
        val mockDisplayMetrics = DisplayMetrics().apply {
            widthPixels = 1080
            heightPixels = 1920
            density = 3.0f
        }
        Dispatchers.setMain(testTransporter)
        DeviceInfoClient.createInstance("", false, HashMap(), "", TestDeviceInfoExtractor(), testTransporterScope)
        SessionClient.createInstance(mock(), mock())
        EventClient.createInstance(TestEventAdapter, testTransporterScope)
        EventClient.onSessionAvailable(MockData.session)
        DimensionConverter.createInstance(0f, mockDisplayMetrics)
        testAaZoneView = AaZoneView(testContext)
    }

    @Test
    fun testStart() {
        val testListener = TestAaZoneViewListener()
        val testAd = (Ad("NewAdId"))
        testAaZoneView.init("TestZoneId")
        testAaZoneView.onStart(testListener)
        testAaZoneView.onAdAvailable(testAd)
        testAaZoneView.onAdLoadedInWebView(testAd)
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        assertEquals(testListener.adLoaded, true)
    }

    @Test
    fun testStartContentListener() {
        val testAdContentListener = TestAdContentListener()
        val testAd = (Ad("NewAdId"))
        testAaZoneView.init("TestZoneId")
        testAaZoneView.onStart(contentListener = testAdContentListener)
        testAaZoneView.onAdAvailable(testAd)
        testAaZoneView.onAdLoadedInWebView(testAd)
        AdContentPublisher.publishContent("TestZoneId", AdContent.createAddToListContent(Ad(payload = Payload(detailedListItems = listOf(
            AddToListItem("trackId", "title", "brand", "cat", "upc", "sku", "disc", "image")
        )))))
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
        testAaZoneView.onAdLoadedInWebView(testAd)
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
        testAaZoneView.onAdLoadedInWebView(Ad("NewAdId"))
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        assertEquals(testListener.adLoaded, true)
    }

    @Test
    fun testOnAdFailed() {
        val testListener = TestAaZoneViewListener()
        testAaZoneView.init("TestZoneId")
        testAaZoneView.onStart(testListener)
        testAaZoneView.onAdLoadInWebViewFailed()
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        assertEquals(testListener.adFailed, true)
    }

    @Test
    fun testOnBlankAdDisplayed() {
        val testListener = TestAaZoneViewListener()
        testAaZoneView.init("TestZoneId")
        testAaZoneView.onStart(testListener)
        testAaZoneView.onBlankAdInWebViewLoaded()
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
        testAaZoneView.onAdLoadedInWebView(Ad("NewAdId"))
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        assertEquals(testListener.adLoaded, true)
    }

    @Test
    fun testOnAdClicked() {
        val testListener = TestAaZoneViewListener()
        val testAd = Ad("NewAdId", actionType = "c")
        testAaZoneView.init("TestZoneId")
        testAaZoneView.onStart(testListener)
        testAaZoneView.onAdLoadedInWebView(testAd)
        testAaZoneView.onAdInWebViewClicked(testAd)
        EventClient.onPublishEvents()
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        assertEquals(testListener.adLoaded, true)
        assert(TestEventAdapter.testSdkEvents.any { event -> event.name == EventStrings.ATL_AD_CLICKED })
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