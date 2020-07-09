package com.adadapted.android.sdk.core.zone

import android.content.Context
import android.content.Intent
import androidx.test.platform.app.InstrumentationRegistry
import com.adadapted.android.sdk.config.EventStrings
import com.adadapted.android.sdk.core.ad.Ad
import com.adadapted.android.sdk.core.ad.AdActionType
import com.adadapted.android.sdk.core.ad.AdEvent
import com.adadapted.android.sdk.core.ad.AdEventClient
import com.adadapted.android.sdk.core.ad.Counter
import com.adadapted.android.sdk.core.concurrency.TransporterCoroutineScope
import com.adadapted.android.sdk.core.device.DeviceInfo
import com.adadapted.android.sdk.core.device.DeviceInfoClient
import com.adadapted.android.sdk.core.device.DeviceInfoClientTest
import com.adadapted.android.sdk.core.event.AppEventClient
import com.adadapted.android.sdk.core.event.TestAppEventSink
import com.adadapted.android.sdk.core.session.Session
import com.adadapted.android.sdk.core.session.SessionClient
import com.adadapted.android.sdk.core.session.SessionTest
import com.adadapted.android.sdk.tools.TestAdEventSink
import com.adadapted.android.sdk.tools.TestTransporter
import com.adadapted.android.sdk.ui.activity.AaWebViewPopupActivity
import com.adadapted.android.sdk.ui.view.AdZonePresenter
import com.adadapted.android.sdk.ui.view.PixelWebView
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
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import java.util.Date
import kotlin.collections.HashMap

@RunWith(RobolectricTestRunner::class)
class AdZonePresenterTest {
    private var mockImpressionIdCounter = mock<Counter>()
    private var mockAdEventSink = mock<TestAdEventSink>()
    private var testContext = InstrumentationRegistry.getInstrumentation().targetContext
    private var mockContext = mock<Context>()
    private lateinit var testAdZonePresenter: AdZonePresenter
    private lateinit var testAaWebViewPopupActivity: AaWebViewPopupActivity
    private var testTransporter = TestCoroutineDispatcher()
    private val testTransporterScope: TransporterCoroutineScope = TestTransporter(testTransporter)
    private var testAppEventSink = TestAppEventSink()
    private var testSession = SessionTest().buildTestSession()
    private var mockSession = Session(DeviceInfo(), "testId", true, true, 30, Date(1907245044), mutableMapOf())

    @Before
    fun setup() {
        whenever(mockAdEventSink.sendBatch(any(),any())).then { }
        whenever(mockImpressionIdCounter.getCurrentCountFor(any())).thenReturn(1)
        whenever(mockContext.applicationContext).thenReturn(mock())

        Dispatchers.setMain(testTransporter)
        DeviceInfoClient.createInstance(testContext,"", false, HashMap(), DeviceInfoClientTest.Companion::requestAdvertisingIdInfo, testTransporterScope)
        SessionClient.createInstance(mock(), mock())
        AdEventClient.createInstance(mockAdEventSink, testTransporterScope, mockImpressionIdCounter)
        AdEventClient.getInstance().onSessionAvailable(mockSession)
        AppEventClient.createInstance(testAppEventSink, testTransporterScope)

        val testIntent = Intent(testContext, AaWebViewPopupActivity::class.java)
        testIntent.putExtra(AaWebViewPopupActivity::class.java.name + ".EXTRA_POPUP_AD", Ad())
        testIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        testAaWebViewPopupActivity = Robolectric.buildActivity<AaWebViewPopupActivity>(AaWebViewPopupActivity::class.java, testIntent)
                .create()
                .resume()
                .get()

        testAdZonePresenter = AdZonePresenter(testContext, PixelWebView(testContext), testAaWebViewPopupActivity)
    }

    @Test
    fun testInitialize() {
        testAdZonePresenter.init("testZoneId")
        AppEventClient.getInstance().onPublishEvents()
        assertEquals(EventStrings.ZONE_LOADED, testAppEventSink.testEvents.first().name)
    }

    @Test
    fun testOnAttach() {
        testAdZonePresenter.init("testZoneId")
        val zones = mapOf<String, Zone>().plus(Pair("testZoneId", Zone("testZoneId", hashMapOf(), listOf(Ad("TestAdId")))))
        testSession.setZones(zones)

        val testListener = TestAdZonePresenterListener()
        testAdZonePresenter.onSessionAvailable(testSession)
        testAdZonePresenter.onAttach(testListener)

        assertEquals("TestAdId", testListener.testAd.id)
    }

    @Test
    fun testOnDetach() {
        testAdZonePresenter.init("testZoneId")
        val zones = mapOf<String, Zone>().plus(Pair("testZoneId", Zone("testZoneId", hashMapOf(), listOf(Ad("TestAdId")))))
        testSession.setZones(zones)

        val testListener = TestAdZonePresenterListener()
        testAdZonePresenter.onSessionAvailable(testSession)
        testAdZonePresenter.onAttach(testListener)
        assertEquals("TestAdId", testListener.testAd.id)

        val testAdEventListener = TestAdEventClientListener()
        AdEventClient.getInstance().addListener(testAdEventListener)

        testAdZonePresenter.onAdDisplayed(Ad("TestAdId"))
        testAdZonePresenter.onDetach()

        assertEquals(AdEvent.Types.IMPRESSION_END, testAdEventListener.testAdEvent?.eventType)
    }

    @Test
    fun testOnAdDisplayed() {
        testAdZonePresenter.init("testZoneId")
        val zones = mapOf<String, Zone>().plus(Pair("testZoneId", Zone("testZoneId", hashMapOf(), listOf(Ad("TestAdId")))))
        testSession.setZones(zones)
        testAdZonePresenter.onSessionAvailable(testSession)

        val testAdEventListener = TestAdEventClientListener()
        AdEventClient.getInstance().addListener(testAdEventListener)
        testAdZonePresenter.onAdDisplayed(Ad("TestAdId"))

        assertEquals(AdEvent.Types.IMPRESSION, testAdEventListener.testAdEvent?.eventType)
    }

   @Test
   fun testOnAdClickedContent() {
       testAdZonePresenter.init("testZoneId")
       val testAd = Ad("TestAdId", "zoneId", "impressionId", "url", AdActionType.CONTENT)
       val zones = mapOf<String, Zone>().plus(Pair("testZoneId", Zone("testZoneId", hashMapOf(), listOf(testAd))))
       testSession.setZones(zones)
       testAdZonePresenter.onSessionAvailable(testSession)

       val testAdEventListener = TestAdEventClientListener()
       AdEventClient.getInstance().addListener(testAdEventListener)
       testAdZonePresenter.onAdDisplayed(testAd)
       testAdZonePresenter.onAdClicked(testAd)

       AppEventClient.getInstance().onPublishEvents()
       assert(testAppEventSink.testEvents.any { event -> event.name == EventStrings.ATL_AD_CLICKED })
   }

    @Test
    fun testOnAdClickedLink() {
        testAdZonePresenter = AdZonePresenter(mockContext, PixelWebView(testContext), testAaWebViewPopupActivity)
        testAdZonePresenter.init("testZoneId")
        val testAd = Ad("TestAdId", "zoneId", "impressionId", "url", AdActionType.LINK)
        val zones = mapOf<String, Zone>().plus(Pair("testZoneId", Zone("testZoneId", hashMapOf(), listOf(testAd))))
        testSession.setZones(zones)
        testAdZonePresenter.onSessionAvailable(testSession)

        val testAdEventListener = TestAdEventClientListener()
        AdEventClient.getInstance().addListener(testAdEventListener)
        testAdZonePresenter.onAdDisplayed(testAd)
        testAdZonePresenter.onAdClicked(testAd)

        assertEquals(AdEvent.Types.INTERACTION, testAdEventListener.testAdEvent?.eventType)
    }

    @Test
    fun testOnAdClickedPopup() {
        testAdZonePresenter.init("testZoneId")
        val testAd = Ad("TestAdId", "zoneId", "impressionId", "url", AdActionType.POPUP)
        val zones = mapOf<String, Zone>().plus(Pair("testZoneId", Zone("testZoneId", hashMapOf(), listOf(testAd))))
        testSession.setZones(zones)
        testAdZonePresenter.onSessionAvailable(testSession)

        val testAdEventListener = TestAdEventClientListener()
        AdEventClient.getInstance().addListener(testAdEventListener)
        testAdZonePresenter.onAdDisplayed(testAd)
        testAdZonePresenter.onAdClicked(testAd)

        assertEquals(AdEvent.Types.INTERACTION, testAdEventListener.testAdEvent?.eventType)
    }

    @Test
    fun testOnAdClickedContentPopup() {
        testAdZonePresenter.init("testZoneId")
        val testAd = Ad("TestAdId", "zoneId", "impressionId", "url", AdActionType.CONTENT_POPUP)
        val zones = mapOf<String, Zone>().plus(Pair("testZoneId", Zone("testZoneId", hashMapOf(), listOf(testAd))))
        testSession.setZones(zones)
        testAdZonePresenter.onSessionAvailable(testSession)

        val testAdEventListener = TestAdEventClientListener()
        AdEventClient.getInstance().addListener(testAdEventListener)
        testAdZonePresenter.onAdDisplayed(testAd)
        testAdZonePresenter.onAdClicked(testAd)

        AppEventClient.getInstance().onPublishEvents()
        assert(testAppEventSink.testEvents.any { event -> event.name == EventStrings.POPUP_AD_CLICKED })
    }

    @Test
    fun testOnSessionAvailable() {
        testAdZonePresenter.init("testZoneId")
        val zones = mapOf<String, Zone>().plus(Pair("testZoneId", Zone("testZoneId", hashMapOf(), listOf(Ad("TestAdId")))))
        testSession.setZones(zones)

        val testListener = TestAdZonePresenterListener()
        testAdZonePresenter.onAttach(testListener)
        testAdZonePresenter.onSessionAvailable(testSession)

        assertEquals("testZoneId", testListener.testZone.id)
    }

    @Test
    fun testOnAdsAvailable() {
        testAdZonePresenter.init("testZoneId")
        val zones = mapOf<String, Zone>().plus(Pair("testZoneId", Zone("testZoneId", hashMapOf(), listOf(Ad("TestAdId")))))
        testSession.setZones(zones)

        val testListener = TestAdZonePresenterListener()
        testAdZonePresenter.onAttach(testListener)
        testAdZonePresenter.onAdsAvailable(testSession)

        assertEquals("testZoneId", testListener.testZone.id)
    }

    @Test
    fun testOnSessioninitFailed() {
        testAdZonePresenter.init("testZoneId")
        val zones = mapOf<String, Zone>().plus(Pair("testZoneId", Zone("testZoneId", hashMapOf(), listOf(Ad("TestAdId")))))
        testSession.setZones(zones)

        val testListener = TestAdZonePresenterListener()
        testAdZonePresenter.onAttach(testListener)
        testAdZonePresenter.onSessionInitFailed()

        assertEquals("NoAdAvail", testListener.testAd.id)
    }
}

class TestAdZonePresenterListener: AdZonePresenter.Listener {
    var testZone = Zone()
    var testAd = Ad()

    override fun onZoneAvailable(zone: Zone) {
        testZone = zone
    }

    override fun onAdsRefreshed(zone: Zone) {
        testZone = zone
    }

    override fun onAdAvailable(ad: Ad) {
        testAd = ad
    }

    override fun onNoAdAvailable() {
        testAd = Ad("NoAdAvail")
    }
}

class TestAdEventClientListener: AdEventClient.Listener {
    var testAdEvent: AdEvent? = null

    override fun onAdEventTracked(event: AdEvent?) {
        testAdEvent = event
    }
}