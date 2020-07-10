package com.adadapted.android.sdk.core.ad

import com.adadapted.android.sdk.core.concurrency.TransporterCoroutineScope
import com.adadapted.android.sdk.core.device.DeviceInfo
import com.adadapted.android.sdk.core.session.Session
import com.adadapted.android.sdk.core.session.SessionClient
import com.adadapted.android.sdk.tools.TestAdEventSink
import com.adadapted.android.sdk.tools.TestTransporter
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import com.nhaarman.mockitokotlin2.whenever
import junit.framework.Assert.assertNotNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test
import java.util.Date

class AdEventClientTest {

    var mockAdEventSink = mock<TestAdEventSink>()
    var testTransporter = TestCoroutineDispatcher()
    val testTransporterScope: TransporterCoroutineScope = TestTransporter(testTransporter)
    var testAdEventClient = AdEventClient
    var mockSession = Session(DeviceInfo(), "testId", true, true, 30, Date(1907245044), mutableMapOf())
    var testAd = Ad("adId", "zoneId", "impId")

    @Before
    fun setup() {
        SessionClient.createInstance(mock(), mock())
        Dispatchers.setMain(testTransporter)
        whenever(mockAdEventSink.sendBatch(any(),any())).then { }

        testAdEventClient.createInstance(mockAdEventSink, testTransporterScope)
        testAdEventClient.getInstance().onSessionAvailable(mockSession)
        testAdEventClient.getInstance().onAdsAvailable(mockSession)
    }

    @Test
    fun createInstance() {
        assertNotNull(testAdEventClient)
    }
    
    @Test
    fun addListenerAndTrackEventImpression() {
        val mockListener = mock<AdEventClient.Listener>()
        testAdEventClient.getInstance().addListener(mockListener)
        testAdEventClient.getInstance().trackImpression(testAd)
        verify(mockListener).onAdEventTracked(any())
    }

    @Test
    fun removeListener() {
        val mockListener = mock<AdEventClient.Listener>()
        testAdEventClient.getInstance().addListener(mockListener)
        testAdEventClient.getInstance().trackImpression(testAd)
        verify(mockListener).onAdEventTracked(any())

        testAdEventClient.getInstance().removeListener(mockListener)
        testAdEventClient.getInstance().trackImpression(testAd)
        verifyZeroInteractions(mockListener)
    }

    @Test
    fun trackImpressionEnd() {
        val mockListener = TestAdEventClientListener()
        testAdEventClient.getInstance().addListener(mockListener)
        testAdEventClient.getInstance().trackImpressionEnd(testAd)
        assert(mockListener.getTrackedEvent()?.eventType == AdEvent.Types.IMPRESSION_END)
    }

    @Test
    fun trackInteraction() {
        val mockListener = TestAdEventClientListener()
        testAdEventClient.getInstance().addListener(mockListener)
        testAdEventClient.getInstance().trackInteraction(testAd)
        assert(mockListener.getTrackedEvent()?.eventType == AdEvent.Types.INTERACTION)
    }

    @Test
    fun trackPopupBegin() {
        val mockListener = TestAdEventClientListener()
        testAdEventClient.getInstance().addListener(mockListener)
        testAdEventClient.getInstance().trackPopupBegin(testAd)
        assert(mockListener.getTrackedEvent()?.eventType == AdEvent.Types.POPUP_BEGIN)
    }

    @Test
    fun trackPopupEnd() {
        val mockListener = TestAdEventClientListener()
        testAdEventClient.getInstance().addListener(mockListener)
        testAdEventClient.getInstance().trackPopupEnd(testAd)
        assert(mockListener.getTrackedEvent()?.eventType == AdEvent.Types.POPUP_END)
    }

    @Test
    fun publishEvents() {
        val mockListener = TestAdEventClientListener()
        testAdEventClient.getInstance().addListener(mockListener)
        testAdEventClient.getInstance().trackInteraction(testAd)

        testAdEventClient.getInstance().onPublishEvents()
        verify(mockAdEventSink).sendBatch(any(), any())
    }

    @Test
    fun onSessionInitFailed() {
        testAdEventClient.getInstance().onSessionInitFailed()

        val mockListener = mock<AdEventClient.Listener>()
        testAdEventClient.getInstance().addListener(mockListener)
        testAdEventClient.getInstance().trackImpression(testAd)
        verify(mockListener).onAdEventTracked(any())
    }
}

class TestAdEventClientListener: AdEventClient.Listener {
    private var trackedEvent: AdEvent? = null

    fun getTrackedEvent(): AdEvent? {
        return trackedEvent
    }

    override fun onAdEventTracked(event: AdEvent?) {
        trackedEvent = event
    }
}