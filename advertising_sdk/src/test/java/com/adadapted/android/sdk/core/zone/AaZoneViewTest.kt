package com.adadapted.android.sdk.core.zone

import androidx.test.platform.app.InstrumentationRegistry
import com.adadapted.android.sdk.config.EventStrings
import com.adadapted.android.sdk.core.ad.Ad
import com.adadapted.android.sdk.core.ad.AdEventClient
import com.adadapted.android.sdk.core.concurrency.TransporterCoroutineScope
import com.adadapted.android.sdk.core.device.DeviceInfo
import com.adadapted.android.sdk.core.device.DeviceInfoClient
import com.adadapted.android.sdk.core.device.DeviceInfoClientTest
import com.adadapted.android.sdk.core.event.AppEventClient
import com.adadapted.android.sdk.core.event.TestAppEventSink
import com.adadapted.android.sdk.core.session.Session
import com.adadapted.android.sdk.core.session.SessionClient
import com.adadapted.android.sdk.tools.TestAdEventSink
import com.adadapted.android.sdk.tools.TestTransporter
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
import java.util.Date
import kotlin.collections.HashMap

@RunWith(RobolectricTestRunner::class)
class AaZoneViewTest {
    private var mockAdEventSink = mock<TestAdEventSink>()
    private var testContext = InstrumentationRegistry.getInstrumentation().targetContext
    private lateinit var testAaZoneView: AaZoneView
    private var testTransporter = TestCoroutineDispatcher()
    private val testTransporterScope: TransporterCoroutineScope = TestTransporter(testTransporter)
    private var testAppEventSink = TestAppEventSink()
    private var mockSession = Session(DeviceInfo(), "testId", true, true, 30, Date(1907245044), mutableMapOf())


    @Before
    fun setup() {
        whenever(mockAdEventSink.sendBatch(any(), any())).then { }

        Dispatchers.setMain(testTransporter)
        DeviceInfoClient.createInstance(testContext,"", false, HashMap(), DeviceInfoClientTest.Companion::requestAdvertisingIdInfo, testTransporterScope)
        SessionClient.createInstance(mock(), mock())
        AdEventClient.createInstance(mockAdEventSink, testTransporterScope)
        AdEventClient.getInstance().onSessionAvailable(mockSession)
        AppEventClient.createInstance(testAppEventSink, testTransporterScope)
        testAaZoneView = AaZoneView(testContext)
    }

    @Test
    fun testInit() {
        testAaZoneView.init("TestZoneId")
        AppEventClient.getInstance().onPublishEvents()
        assertEquals(EventStrings.ZONE_LOADED, testAppEventSink.testEvents.first().name)
    }

    @Test
    fun testStart() {
        val testListener = TestAaZoneViewListener()
        val testAd = (Ad("NewAdId"))
        testAaZoneView.init("TestZoneId")
        testAaZoneView.onStart(testListener)
        testAaZoneView.onAdAvailable(testAd)
        testAaZoneView.onAdLoaded(testAd)

        assertEquals(testListener.adLoaded, true)
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

        assertEquals(testListener.zoneHasAds, true)
    }

    @Test
    fun testOnAdsRefreshed() {
        val testListener = TestAaZoneViewListener()
        testAaZoneView.init("TestZoneId")
        testAaZoneView.onStart(testListener)
        testAaZoneView.onAdsRefreshed(Zone("TestZoneId",ads = listOf(Ad("NewZoneAdId"))))

        assertEquals(testListener.zoneHasAds, true)
    }

    @Test
    fun testOnAdLoaded() {
        val testListener = TestAaZoneViewListener()
        testAaZoneView.init("TestZoneId")
        testAaZoneView.onStart(testListener)
        testAaZoneView.onAdLoaded(Ad("NewAdId"))

        assertEquals(testListener.adLoaded, true)
    }

    @Test
    fun testOnAdFailed() {
        val testListener = TestAaZoneViewListener()
        testAaZoneView.init("TestZoneId")
        testAaZoneView.onStart(testListener)
        testAaZoneView.onAdLoadFailed()

        assertEquals(testListener.adFailed, true)
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