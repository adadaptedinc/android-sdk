package com.adadapted.android.sdk.core.zone

import android.content.Context
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import com.adadapted.android.sdk.constants.EventStrings
import com.adadapted.android.sdk.core.ad.Ad
import com.adadapted.android.sdk.core.ad.AdActionType
import com.adadapted.android.sdk.core.ad.AdClient
import com.adadapted.android.sdk.core.ad.AdZoneData
import com.adadapted.android.sdk.core.atl.AddToListItem
import com.adadapted.android.sdk.core.concurrency.TransporterCoroutineScope
import com.adadapted.android.sdk.core.device.DeviceInfoClient
import com.adadapted.android.sdk.core.event.AdEvent
import com.adadapted.android.sdk.core.event.AdEventTypes
import com.adadapted.android.sdk.core.event.EventClient
import com.adadapted.android.sdk.core.interfaces.AdAdapter
import com.adadapted.android.sdk.core.interfaces.EventClientListener
import com.adadapted.android.sdk.core.interfaces.ZoneAdListener
import com.adadapted.android.sdk.core.payload.Payload
import com.adadapted.android.sdk.core.session.SessionClient
import com.adadapted.android.sdk.core.view.AaWebViewPopupActivity
import com.adadapted.android.sdk.core.view.AdViewHandler
import com.adadapted.android.sdk.core.view.AdWebView
import com.adadapted.android.sdk.core.view.AdZonePresenter
import com.adadapted.android.sdk.core.view.AdZonePresenterListener
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

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class AdZonePresenterTest {
    private var testContext = InstrumentationRegistry.getInstrumentation().targetContext
    private var mockContext = mock<Context>()
    private lateinit var testAdZonePresenter: AdZonePresenter
    private lateinit var testAaWebViewPopupActivity: AaWebViewPopupActivity
    private var testTransporter = UnconfinedTestDispatcher()
    private val testTransporterScope: TransporterCoroutineScope = TestTransporter(testTransporter)
    private var mockWebView: AdWebView? = null
    private var testAdAdapter: TestAdAdapter = TestAdAdapter()

    @Before
    fun setup() {
        whenever(mockContext.applicationContext).thenReturn(mock())
        whenever(mockContext.resources).thenReturn(mock())
        whenever(mockContext.resources.displayMetrics).thenReturn(mock())
        mockWebView = AdWebView(ApplicationProvider.getApplicationContext(), mock())
        mockWebView?.loaded = true
        testAdAdapter.setMockData(
            AdZoneData(
                Ad(
                    id = "TestAdId", "123", "testUrl", "action", "path", Payload(
                        "payloadId", "msg", "img", "campaign", "appid", 0, listOf(
                            AddToListItem(
                                "trackingId",
                                "TestAdItem",
                                "brand",
                                "category",
                                "upc",
                                "sku",
                                "retail",
                                "img"
                            )
                        )
                    )
                )
            )
        )

        Dispatchers.setMain(testTransporter)
        DeviceInfoClient.createInstance("", false, HashMap(), "", TestDeviceInfoExtractor(), testTransporterScope)
        SessionClient.onStart(mock())
        AdClient.createInstance(testAdAdapter, testTransporterScope)
        EventClient.createInstance(TestEventAdapter, testTransporterScope)

        val testIntent = Intent(testContext, AaWebViewPopupActivity::class.java)
        testIntent.putExtra(AaWebViewPopupActivity::class.java.name + ".EXTRA_POPUP_AD", Json.encodeToString(
            serializer(), Ad()))
        testIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        testAaWebViewPopupActivity = Robolectric.buildActivity(AaWebViewPopupActivity::class.java, testIntent)
                .create()
                .resume()
                .get()

        testAdZonePresenter = AdZonePresenter(AdViewHandler(testContext), AdClient)
    }

    @Test
    fun testOnAttach() {
        testAdZonePresenter.init("testZoneId", mockWebView!!)
        val testListener = TestAdZonePresenterListener()
        testAdZonePresenter.onAttach(testListener)

        assertEquals("TestAdId", testListener.testAd.id)
    }

    @Test
    fun testOnDetach() {
        testAdZonePresenter.init("testZoneId", mockWebView!!)

        val testListener = TestAdZonePresenterListener()
        testAdZonePresenter.onAttach(testListener)
        assertEquals("TestAdId", testListener.testAd.id)

        val testAdEventListener = TestAdEventClientListener()
        EventClient.addListener(testAdEventListener)

        testAdZonePresenter.onAdDisplayed(Ad("TestAdId"), true)
        testAdZonePresenter.onDetach()

        assertEquals("TestAdId", testListener.testAd.id)
    }

    @Test
    fun testOnAdDisplayed() {
        testAdZonePresenter.init("testZoneId", mockWebView!!)

        val testAdEventListener = TestAdEventClientListener()
        EventClient.addListener(testAdEventListener)
        testAdZonePresenter.onAdDisplayed(Ad("TestAdId"), true)

        assertEquals(AdEventTypes.IMPRESSION, testAdEventListener.testAdEvent?.eventType)
    }

    @Test
    fun testOnAdDisplayedButZoneNotVisible() {
        testAdZonePresenter.init("testZoneId", mockWebView!!)

        val testAdEventListener = TestAdEventClientListener()
        EventClient.addListener(testAdEventListener)
        testAdZonePresenter.onAdDisplayed(Ad("TestAdId"), false)

        assert(testAdEventListener.testAdEvent == null)
    }

    @Test
    fun testOnAdCompletedButZoneNotVisible() {
        testAdZonePresenter.init("testZoneId", mockWebView!!)
        val testAd = Ad(id = "TestAdId")
        val testAdEventListener = TestAdEventClientListener()
        EventClient.addListener(testAdEventListener)
        testAdZonePresenter.onAdDisplayed(testAd, false)
        testAdZonePresenter.onAttach(object : AdZonePresenterListener{
            override fun onZoneAvailable(adZoneData: AdZoneData) {}
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
        testAdZonePresenter.init("testZoneId", mockWebView!!)
        val testAd = Ad(id = "TestAdId")
        val testAdEventListener = TestAdEventClientListener()
        EventClient.addListener(testAdEventListener)
        testAdZonePresenter.onAdDisplayed(testAd, false)
        testAdZonePresenter.onAttach(object : AdZonePresenterListener{
            override fun onZoneAvailable(adZoneData: AdZoneData) {}
            override fun onAdAvailable(ad: Ad) {}
            override fun onNoAdAvailable() {}
            override fun onAdVisibilityChanged(ad: Ad) {}
        })
        testAdZonePresenter.onAdClicked(testAd)

        assertEquals(null, testAdEventListener.testAdEvent)
    }

   @Test
   fun testOnAdClickedContent() {
       testAdZonePresenter.init("testZoneId", mockWebView!!)
       val testAd = Ad("TestAdId", "impressionId", "url", AdActionType.CONTENT)
       val testAdEventListener = TestAdEventClientListener()
       EventClient.addListener(testAdEventListener)
       testAdZonePresenter.onAdDisplayed(testAd, true)
       testAdZonePresenter.onAdClicked(testAd)

       EventClient.onPublishEvents()
       assert(TestEventAdapter.testSdkEvents.any { event -> event.name == EventStrings.ATL_AD_CLICKED })
   }

    @Test
    fun testOnAdClickedLink() {
        testAdZonePresenter.init("testZoneId", mockWebView!!)
        val testAd = Ad("TestAdId", "impressionId", "url", AdActionType.LINK)
        val testAdEventListener = TestAdEventClientListener()
        EventClient.addListener(testAdEventListener)
        testAdZonePresenter.onAdDisplayed(testAd, true)
        testAdZonePresenter.onAdClicked(testAd)

        assertEquals(AdEventTypes.INTERACTION, testAdEventListener.testAdEvent?.eventType)
    }

    @Test
    fun testOnAdClickedPopup() {
        testAdZonePresenter.init("testZoneId", mockWebView!!)
        val testAd = Ad("TestAdId", "impressionId", "url", AdActionType.POPUP)
        val testAdEventListener = TestAdEventClientListener()
        EventClient.addListener(testAdEventListener)
        testAdZonePresenter.onAdDisplayed(testAd, true)
        testAdZonePresenter.onAdClicked(testAd)

        assertEquals(AdEventTypes.INTERACTION, testAdEventListener.testAdEvent?.eventType)
    }

    @Test
    fun testOnAdClickedContentPopup() {
        testAdZonePresenter.init("testZoneId", mockWebView!!)
        val testAd = Ad("TestAdId", "impressionId", "url", AdActionType.CONTENT_POPUP)
        val testAdEventListener = TestAdEventClientListener()
        EventClient.addListener(testAdEventListener)
        testAdZonePresenter.onAdDisplayed(testAd, true)
        testAdZonePresenter.onAdClicked(testAd)

        EventClient.onPublishEvents()
        assert(TestEventAdapter.testSdkEvents.any { event -> event.name == EventStrings.POPUP_AD_CLICKED })
    }

    @Test
    fun testNullListener() {
        testAdZonePresenter.init("testZoneId", mockWebView!!)
        testAdZonePresenter.onAttach(null)

        assertNotNull(testAdZonePresenter)
    }
}

class TestAdAdapter: AdAdapter {
    private var adZoneData: AdZoneData = AdZoneData()

    fun setMockData(adZoneData: AdZoneData) {
        this.adZoneData = adZoneData
    }

    override suspend fun requestAd(
        zoneId: String,
        listener: ZoneAdListener,
        storeId: String,
        contextId: String,
        extra: String
    ) {
        listener.onAdLoaded(adZoneData)
    }
}

class TestAdZonePresenterListener: AdZonePresenterListener {
    var testZoneData = AdZoneData()
    var testAd = Ad()

    override fun onZoneAvailable(adZoneData: AdZoneData) {
        testZoneData = adZoneData
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
