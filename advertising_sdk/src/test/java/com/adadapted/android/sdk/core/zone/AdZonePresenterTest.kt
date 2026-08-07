package com.adadapted.android.sdk.core.zone

import android.content.Context
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import com.adadapted.android.sdk.constants.Config as SdkConfig
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
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33]) //temp until java21
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
        SessionClient.createOrResumeSession()
        AdClient.createInstance(testAdAdapter, testTransporterScope)
        EventClient.createInstance(TestEventAdapter, testTransporterScope)
        EventClient.onPublishEvents()
        TestEventAdapter.cleanupEvents()

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

    @After
    fun tearDown() {
        //These tests share one virtual clock and AdClient is a singleton, so a repeating zone timer
        //left running would fire ad requests against the next test's adapter
        testAdZonePresenter.onDetach()
    }

    //The virtual clock runs in milliseconds, the refresh times under test are in seconds
    private fun advanceTimeBySeconds(seconds: Long) =
        testTransporter.scheduler.advanceTimeBy(TimeUnit.SECONDS.toMillis(seconds))

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
    fun zoneTimerRefetchesOnTheServerSuppliedRefreshTime() {
        val serverRefreshSeconds = 30L //Faster than the default, above the floor
        val testAd = Ad(id = "TestAdId", impressionId = "123", refreshTime = serverRefreshSeconds)
        testAdAdapter.setMockData(AdZoneData(testAd))
        testAdZonePresenter.init("testZoneId", mockWebView!!)
        testAdZonePresenter.onAttach(TestAdZonePresenterListener())
        testAdZonePresenter.onAdDisplayed(testAd, true)

        val requestsBeforeRefresh = testAdAdapter.requestCount
        advanceTimeBySeconds(serverRefreshSeconds - 1)
        assertEquals(
            "Should not have refreshed before the server's refresh time elapsed",
            requestsBeforeRefresh,
            testAdAdapter.requestCount
        )

        advanceTimeBySeconds(2)
        assertEquals(
            "Should have refreshed once the server's refresh time elapsed",
            requestsBeforeRefresh + 1,
            testAdAdapter.requestCount
        )
    }

    @Test
    fun zoneTimerHonorsAServerRefreshTimeSlowerThanTheDefault() {
        val serverRefreshSeconds = 70L //Slower than the 60 second default
        val testAd = Ad(id = "TestAdId", impressionId = "123", refreshTime = serverRefreshSeconds)
        testAdAdapter.setMockData(AdZoneData(testAd))
        testAdZonePresenter.init("testZoneId", mockWebView!!)
        testAdZonePresenter.onAttach(TestAdZonePresenterListener())
        testAdZonePresenter.onAdDisplayed(testAd, true)

        val requestsBeforeRefresh = testAdAdapter.requestCount
        advanceTimeBySeconds(SdkConfig.DEFAULT_AD_REFRESH_SECONDS + 1)
        assertEquals(
            "Should not have refreshed at the default refresh time when the server asked for a slower one",
            requestsBeforeRefresh,
            testAdAdapter.requestCount
        )

        advanceTimeBySeconds(serverRefreshSeconds - SdkConfig.DEFAULT_AD_REFRESH_SECONDS)
        assertEquals(
            "Should have refreshed once the server's slower refresh time elapsed",
            requestsBeforeRefresh + 1,
            testAdAdapter.requestCount
        )
    }

    @Test
    fun zoneTimerDoesNotRefreshFasterThanTheFloorWhenTheServerAsksItTo() {
        val testAd = Ad(id = "TestAdId", impressionId = "123", refreshTime = 1) //Below the floor
        testAdAdapter.setMockData(AdZoneData(testAd))
        testAdZonePresenter.init("testZoneId", mockWebView!!)
        testAdZonePresenter.onAttach(TestAdZonePresenterListener())
        testAdZonePresenter.onAdDisplayed(testAd, true)

        val requestsBeforeRefresh = testAdAdapter.requestCount
        advanceTimeBySeconds(Ad.MINIMUM_REFRESH_TIME_SECONDS - 1)
        assertEquals(
            "Should not have refreshed faster than the floor the below-floor value clamps up to",
            requestsBeforeRefresh,
            testAdAdapter.requestCount
        )

        advanceTimeBySeconds(2)
        assertEquals(
            "Should have refreshed once the floor elapsed, not waited out the default",
            requestsBeforeRefresh + 1,
            testAdAdapter.requestCount
        )
    }

    //A no-fill carries the server's backoff on an otherwise empty Ad. In production the response
    //lands after onAttach returns, so the zone timer is first armed from the blank-displayed
    //callback - which must not have discarded the served refresh by then.
    @Test
    fun noFillBacksOffOnTheServedRefreshTimeWhenTheResponseLandsAfterAttach() {
        val serverRefreshSeconds = 300L
        val noFill = Ad(refreshTime = serverRefreshSeconds)
        val silentAdapter = SilentAdAdapter()
        AdClient.createInstance(silentAdapter, testTransporterScope)
        testAdZonePresenter.init("testZoneId", mockWebView!!)
        testAdZonePresenter.onAttach(TestAdZonePresenterListener()) //Fetch dispatched, no response yet

        testAdZonePresenter.onAdLoaded(AdZoneData(noFill)) //Response lands late
        testAdZonePresenter.onBlankDisplayed() //View finished loading blank

        val requestsBeforeRefresh = silentAdapter.requestCount
        advanceTimeBySeconds(SdkConfig.DEFAULT_AD_REFRESH_SECONDS + 1)
        assertEquals(
            "Should not have refetched at the default when the server asked to back off",
            requestsBeforeRefresh,
            silentAdapter.requestCount
        )

        advanceTimeBySeconds(serverRefreshSeconds - SdkConfig.DEFAULT_AD_REFRESH_SECONDS)
        assertEquals(
            "Should have refetched once the server's backoff elapsed",
            requestsBeforeRefresh + 1,
            silentAdapter.requestCount
        )
    }

    //A no-fill is a valid response carrying an empty Ad, and the host app can only hide the zone if
    //the refetch reports it the way the first fetch does.
    @Test
    fun refreshingIntoANoFillReportsTheZoneAsHavingNoAds() {
        val servedRefreshSeconds = 30L
        val noFillRefreshSeconds = 300L
        val servedAd = Ad(id = "TestAdId", impressionId = "123", refreshTime = servedRefreshSeconds)
        val noFillAdapter = NoFillAfterFirstAdAdapter(servedAd, Ad(refreshTime = noFillRefreshSeconds))
        AdClient.createInstance(noFillAdapter, testTransporterScope)
        testAdZonePresenter.init("testZoneId", mockWebView!!)
        val testListener = TestAdZonePresenterListener()
        testAdZonePresenter.onAttach(testListener)
        testAdZonePresenter.onAdDisplayed(servedAd, true)
        assertTrue("The zone should start out reported as filled", testListener.testZoneData.hasAd())

        advanceTimeBySeconds(servedRefreshSeconds + 1) //Refetch fires and comes back a no-fill

        assertFalse(
            "A no-fill on refresh should report the zone as having no ads instead of leaving the host app on the previous ad",
            testListener.testZoneData.hasAd()
        )

        val requestsAfterNoFill = noFillAdapter.requestCount
        advanceTimeBySeconds(SdkConfig.DEFAULT_AD_REFRESH_SECONDS + 1)
        assertEquals(
            "The no-fill's served refresh should back off the next fetch rather than polling on the default",
            requestsAfterNoFill,
            noFillAdapter.requestCount
        )

        advanceTimeBySeconds(noFillRefreshSeconds - SdkConfig.DEFAULT_AD_REFRESH_SECONDS)
        assertEquals(
            "Should have refetched once the no-fill's backoff elapsed",
            requestsAfterNoFill + 1,
            noFillAdapter.requestCount
        )
    }

    @Test
    fun zoneTimerWaitsForTheDefaultRefreshTimeWhenTheServerSuppliesNone() {
        val testAd = Ad(id = "TestAdId", impressionId = "123")
        testAdAdapter.setMockData(AdZoneData(testAd))
        testAdZonePresenter.init("testZoneId", mockWebView!!)
        testAdZonePresenter.onAttach(TestAdZonePresenterListener())
        testAdZonePresenter.onAdDisplayed(testAd, true)

        val requestsBeforeRefresh = testAdAdapter.requestCount
        advanceTimeBySeconds(SdkConfig.DEFAULT_AD_REFRESH_SECONDS / 2)
        assertEquals(
            "Should not have refreshed halfway through the default refresh time",
            requestsBeforeRefresh,
            testAdAdapter.requestCount
        )

        advanceTimeBySeconds(SdkConfig.DEFAULT_AD_REFRESH_SECONDS / 2 + 1)
        assertEquals(
            "Should have refreshed once the default refresh time elapsed",
            requestsBeforeRefresh + 1,
            testAdAdapter.requestCount
        )
    }

    //A refetch that fails hands the presenter an empty Ad. The served backoff has to survive that,
    //or a server that asked to be hit every 300s gets hit every 60s from the first failure onward.
    @Test
    fun aFailedRefetchKeepsTheServedRefreshTimeInsteadOfDroppingToTheDefault() {
        val serverRefreshSeconds = 300L
        val servedAd = Ad(id = "TestAdId", impressionId = "123", refreshTime = serverRefreshSeconds)
        val failingAdapter = FailAfterFirstAdAdapter(servedAd)
        AdClient.createInstance(failingAdapter, testTransporterScope)
        testAdZonePresenter.init("testZoneId", mockWebView!!)
        testAdZonePresenter.onAttach(TestAdZonePresenterListener())
        testAdZonePresenter.onAdDisplayed(servedAd, true)

        advanceTimeBySeconds(serverRefreshSeconds + 1) //First refetch fires on the served time, and fails
        assertEquals(
            "The first refetch should have fired once the served refresh time elapsed",
            2,
            failingAdapter.requestCount
        )

        advanceTimeBySeconds(SdkConfig.DEFAULT_AD_REFRESH_SECONDS + 1)
        assertEquals(
            "A failed refetch should not drop the zone back to the default refresh time",
            2,
            failingAdapter.requestCount
        )

        advanceTimeBySeconds(serverRefreshSeconds - SdkConfig.DEFAULT_AD_REFRESH_SECONDS)
        assertEquals(
            "Should have refetched again once the served refresh time elapsed a second time",
            3,
            failingAdapter.requestCount
        )
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
    var requestCount = 0

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
        requestCount++
        listener.onAdLoaded(adZoneData)
    }
}

//Serves the Ad once and fails every fetch after it, the shape of a zone that loaded and then lost
//the server
class FailAfterFirstAdAdapter(private val ad: Ad): AdAdapter {
    var requestCount = 0

    override suspend fun requestAd(
        zoneId: String,
        listener: ZoneAdListener,
        storeId: String,
        contextId: String,
        extra: String
    ) {
        requestCount++
        if (requestCount == 1) {
            listener.onAdLoaded(AdZoneData(ad))
        } else {
            listener.onAdLoadFailed()
        }
    }
}

//Serves the Ad once and no-fills every fetch after it, the shape of a zone whose campaign ran out
class NoFillAfterFirstAdAdapter(private val ad: Ad, private val noFill: Ad): AdAdapter {
    var requestCount = 0

    override suspend fun requestAd(
        zoneId: String,
        listener: ZoneAdListener,
        storeId: String,
        contextId: String,
        extra: String
    ) {
        requestCount++
        listener.onAdLoaded(AdZoneData(if (requestCount == 1) ad else noFill))
    }
}

//Dispatches nothing back, so the presenter stays unloaded until a response is delivered by hand
class SilentAdAdapter: AdAdapter {
    var requestCount = 0

    override suspend fun requestAd(
        zoneId: String,
        listener: ZoneAdListener,
        storeId: String,
        contextId: String,
        extra: String
    ) {
        requestCount++
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
