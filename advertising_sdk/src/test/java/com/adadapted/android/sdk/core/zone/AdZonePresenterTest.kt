package com.adadapted.android.sdk.core.zone

import android.content.Context
import android.content.Intent
import androidx.test.platform.app.InstrumentationRegistry
import com.adadapted.android.sdk.constants.EventStrings
import com.adadapted.android.sdk.core.ad.Ad
import com.adadapted.android.sdk.core.ad.AdActionType
import com.adadapted.android.sdk.core.concurrency.TransporterCoroutineScope
import com.adadapted.android.sdk.core.device.DeviceInfo
import com.adadapted.android.sdk.core.device.DeviceInfoClient
import com.adadapted.android.sdk.core.event.AdEvent
import com.adadapted.android.sdk.core.event.AdEventTypes
import com.adadapted.android.sdk.core.event.EventClient
import com.adadapted.android.sdk.core.interfaces.EventClientListener
import com.adadapted.android.sdk.core.session.SessionClient
import com.adadapted.android.sdk.core.session.SessionTest
import com.adadapted.android.sdk.core.view.AaWebViewPopupActivity
import com.adadapted.android.sdk.core.view.AdViewHandler
import com.adadapted.android.sdk.core.view.AdZonePresenter
import com.adadapted.android.sdk.core.view.AdZonePresenterListener
import com.adadapted.android.sdk.core.view.Zone
import com.adadapted.android.sdk.tools.MockData
import com.adadapted.android.sdk.tools.TestDeviceInfoExtractor
import com.adadapted.android.sdk.tools.TestEventAdapter
import com.adadapted.android.sdk.tools.TestTransporter
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.setMain
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import kotlin.collections.HashMap

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class AdZonePresenterTest {
    private var testContext = InstrumentationRegistry.getInstrumentation().targetContext
    private var mockContext = mock<Context>()
    private lateinit var testAdZonePresenter: AdZonePresenter
    private lateinit var testAaWebViewPopupActivity: AaWebViewPopupActivity
    private var testTransporter = UnconfinedTestDispatcher()
    private val testTransporterScope: TransporterCoroutineScope = TestTransporter(testTransporter)
    private var testSession = SessionTest().buildTestSession()

    @Before
    fun setup() {
        MockData.session.deviceInfo = DeviceInfo()
        whenever(mockContext.applicationContext).thenReturn(mock())

        Dispatchers.setMain(testTransporter)
        DeviceInfoClient.createInstance("", false, HashMap(), "", TestDeviceInfoExtractor(), testTransporterScope)
        SessionClient.createInstance(mock(), mock())
        EventClient.createInstance(TestEventAdapter, testTransporterScope)
        EventClient.onSessionAvailable(MockData.session)

        val testIntent = Intent(testContext, AaWebViewPopupActivity::class.java)
        testIntent.putExtra(AaWebViewPopupActivity::class.java.name + ".EXTRA_POPUP_AD", Json.encodeToString(
            serializer(), Ad()))
        testIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        testAaWebViewPopupActivity = Robolectric.buildActivity(AaWebViewPopupActivity::class.java, testIntent)
                .create()
                .resume()
                .get()

        testAdZonePresenter = AdZonePresenter(AdViewHandler(testContext), SessionClient)
    }

    @Test
    fun testOnAttach() {
        testAdZonePresenter.init("testZoneId")
        val zones = mapOf<String, Zone>().plus(Pair("testZoneId", Zone("testZoneId", listOf(Ad("TestAdId")))))
        testSession.updateZones(zones)

        val testListener = TestAdZonePresenterListener()
        testAdZonePresenter.onSessionAvailable(testSession)
        testAdZonePresenter.onAttach(testListener)

        assertEquals("TestAdId", testListener.testAd.id)
    }

    @Test
    fun testOnDetach() {
        testAdZonePresenter.init("testZoneId")
        val zones = mapOf<String, Zone>().plus(Pair("testZoneId", Zone("testZoneId", listOf(Ad("TestAdId")))))
        testSession.updateZones(zones)

        val testListener = TestAdZonePresenterListener()
        testAdZonePresenter.onSessionAvailable(testSession)
        testAdZonePresenter.onAttach(testListener)
        assertEquals("TestAdId", testListener.testAd.id)

        val testAdEventListener = TestAdEventClientListener()
        EventClient.addListener(testAdEventListener)

        testAdZonePresenter.onAdDisplayed(Ad("TestAdId"), true)
        testAdZonePresenter.onDetach()

        assertEquals("", testListener.testZone.id)
    }

    @Test
    fun testOnAdDisplayed() {
        testAdZonePresenter.init("testZoneId")
        val zones = mapOf<String, Zone>().plus(Pair("testZoneId", Zone("testZoneId", listOf(Ad("TestAdId")))))
        testSession.updateZones(zones)
        testAdZonePresenter.onSessionAvailable(testSession)

        val testAdEventListener = TestAdEventClientListener()
        EventClient.addListener(testAdEventListener)
        testAdZonePresenter.onAdDisplayed(Ad("TestAdId"), true)

        assertEquals(AdEventTypes.IMPRESSION, testAdEventListener.testAdEvent?.eventType)
    }

    @Test
    fun testOnAdDisplayedButZoneNotVisible() {
        testAdZonePresenter.init("testZoneId")
        val zones = mapOf<String, Zone>().plus(Pair("testZoneId", Zone("testZoneId", listOf(Ad("TestAdId")))))
        testSession.updateZones(zones)
        testAdZonePresenter.onSessionAvailable(testSession)

        val testAdEventListener = TestAdEventClientListener()
        EventClient.addListener(testAdEventListener)
        testAdZonePresenter.onAdDisplayed(Ad("TestAdId"), false)

        assert(testAdEventListener.testAdEvent == null)
    }

    @Test
    fun testOnAdCompletedButZoneNotVisible() {
        testAdZonePresenter.init("testZoneId")
        val testAd = Ad(id = "TestAdId")
        val zones = mapOf<String, Zone>().plus(Pair("testZoneId", Zone("testZoneId", listOf(testAd, testAd))))
        testSession.updateZones(zones)
        testAdZonePresenter.onSessionAvailable(testSession)

        val testAdEventListener = TestAdEventClientListener()
        EventClient.addListener(testAdEventListener)
        testAdZonePresenter.onAdDisplayed(testAd, false)
        testAdZonePresenter.onAttach(object : AdZonePresenterListener{
            override fun onZoneAvailable(zone: Zone) {}
            override fun onAdsRefreshed(zone: Zone) {}
            override fun onAdAvailable(ad: Ad) {}
            override fun onNoAdAvailable() {}
            override fun onAdVisibilityChanged(ad: Ad) {}
        })
        testAdZonePresenter.onAdClicked(testAd)
        testAdZonePresenter.onAdDisplayed(testAd, false)
        testAdZonePresenter.onAdClicked(testAd)

        assertEquals(AdEventTypes.INVISIBLE_IMPRESSION, testAdEventListener.testAdEvent?.eventType)
    }

    @Test
    fun testAdNotCompletedBecauseThereIsOnlyOne() {
        testAdZonePresenter.init("testZoneId")
        val testAd = Ad(id = "TestAdId")
        val zones = mapOf<String, Zone>().plus(Pair("testZoneId", Zone("testZoneId", listOf(testAd))))
        testSession.updateZones(zones)
        testAdZonePresenter.onSessionAvailable(testSession)

        val testAdEventListener = TestAdEventClientListener()
        EventClient.addListener(testAdEventListener)
        testAdZonePresenter.onAdDisplayed(testAd, false)
        testAdZonePresenter.onAttach(object : AdZonePresenterListener{
            override fun onZoneAvailable(zone: Zone) {}
            override fun onAdsRefreshed(zone: Zone) {}
            override fun onAdAvailable(ad: Ad) {}
            override fun onNoAdAvailable() {}
            override fun onAdVisibilityChanged(ad: Ad) {}
        })
        testAdZonePresenter.onAdClicked(testAd)

        assertEquals(null, testAdEventListener.testAdEvent)
    }

   @Test
   fun testOnAdClickedContent() {
       testAdZonePresenter.init("testZoneId")
       val testAd = Ad("TestAdId", "impressionId", "url", AdActionType.CONTENT)
       val zones = mapOf<String, Zone>().plus(Pair("testZoneId", Zone("testZoneId", listOf(testAd))))
       testSession.updateZones(zones)
       testAdZonePresenter.onSessionAvailable(testSession)

       val testAdEventListener = TestAdEventClientListener()
       EventClient.addListener(testAdEventListener)
       testAdZonePresenter.onAdDisplayed(testAd, true)
       testAdZonePresenter.onAdClicked(testAd)

       EventClient.onPublishEvents()
       assert(TestEventAdapter.testSdkEvents.any { event -> event.name == EventStrings.ATL_AD_CLICKED })
   }

    @Test
    fun testOnAdClickedLink() {
        testAdZonePresenter = AdZonePresenter(AdViewHandler(mockContext), SessionClient)
        testAdZonePresenter.init("testZoneId")
        val testAd = Ad("TestAdId", "impressionId", "url", AdActionType.LINK)
        val zones = mapOf<String, Zone>().plus(Pair("testZoneId", Zone("testZoneId", listOf(testAd))))
        testSession.updateZones(zones)
        testAdZonePresenter.onSessionAvailable(testSession)

        val testAdEventListener = TestAdEventClientListener()
        EventClient.addListener(testAdEventListener)
        testAdZonePresenter.onAdDisplayed(testAd, true)
        testAdZonePresenter.onAdClicked(testAd)

        assertEquals(AdEventTypes.INTERACTION, testAdEventListener.testAdEvent?.eventType)
    }

    @Test
    fun testOnAdClickedPopup() {
        testAdZonePresenter.init("testZoneId")
        val testAd = Ad("TestAdId", "impressionId", "url", AdActionType.POPUP)
        val zones = mapOf<String, Zone>().plus(Pair("testZoneId", Zone("testZoneId", listOf(testAd))))
        testSession.updateZones(zones)
        testAdZonePresenter.onSessionAvailable(testSession)

        val testAdEventListener = TestAdEventClientListener()
        EventClient.addListener(testAdEventListener)
        testAdZonePresenter.onAdDisplayed(testAd, true)
        testAdZonePresenter.onAdClicked(testAd)

        assertEquals(AdEventTypes.INTERACTION, testAdEventListener.testAdEvent?.eventType)
    }

    @Test
    fun testOnAdClickedContentPopup() {
        testAdZonePresenter.init("testZoneId")
        val testAd = Ad("TestAdId", "impressionId", "url", AdActionType.CONTENT_POPUP)
        val zones = mapOf<String, Zone>().plus(Pair("testZoneId", Zone("testZoneId", listOf(testAd))))
        testSession.updateZones(zones)
        testAdZonePresenter.onSessionAvailable(testSession)

        val testAdEventListener = TestAdEventClientListener()
        EventClient.addListener(testAdEventListener)
        testAdZonePresenter.onAdDisplayed(testAd, true)
        testAdZonePresenter.onAdClicked(testAd)

        EventClient.onPublishEvents()
        assert(TestEventAdapter.testSdkEvents.any { event -> event.name == EventStrings.POPUP_AD_CLICKED })
    }

    @Test
    fun testOnSessionAvailable() {
        testAdZonePresenter.init("testZoneId")
        val zones = mapOf<String, Zone>().plus(Pair("testZoneId", Zone("testZoneId", listOf(Ad("TestAdId")))))
        testSession.updateZones(zones)

        val testListener = TestAdZonePresenterListener()
        testAdZonePresenter.onAttach(testListener)
        testAdZonePresenter.onSessionAvailable(testSession)

        assertEquals("testZoneId", testListener.testZone.id)
    }

    @Test
    fun testOnAdsAvailable() {
        testAdZonePresenter.init("testZoneId")
        val zones = mapOf<String, Zone>().plus(Pair("testZoneId", Zone("testZoneId", listOf(Ad("TestAdId")))))
        testSession.updateZones(zones)

        val testListener = TestAdZonePresenterListener()
        testAdZonePresenter.onAttach(testListener)
        testAdZonePresenter.onAdsAvailable(testSession)

        assertEquals("testZoneId", testListener.testZone.id)
    }

    @Test
    fun testOnSessioninitFailed() {
        testAdZonePresenter.init("testZoneId")
        val zones = mapOf<String, Zone>().plus(Pair("testZoneId", Zone("testZoneId", listOf(Ad("TestAdId")))))
        testSession.updateZones(zones)

        val testListener = TestAdZonePresenterListener()
        testAdZonePresenter.onAttach(testListener)
        testAdZonePresenter.onSessionInitFailed()

        assertEquals("NoAdAvail", testListener.testAd.id)
    }

    @Test
    fun testNullListener() {
        testAdZonePresenter.init("testZoneId")
        val zones = mapOf<String, Zone>().plus(Pair("testZoneId", Zone("testZoneId", listOf(Ad("TestAdId")))))
        testSession.updateZones(zones)

        testAdZonePresenter.onSessionAvailable(testSession)
        testAdZonePresenter.onAttach(null)

        assertNotNull(testAdZonePresenter)
    }
}

class TestAdZonePresenterListener: AdZonePresenterListener {
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

    override fun onAdVisibilityChanged(ad: Ad) {
        testAd = ad
    }
}

class TestAdEventClientListener: EventClientListener {
    var testAdEvent: AdEvent? = null

    override fun onAdEventTracked(event: AdEvent?) {
        testAdEvent = event
    }
}
