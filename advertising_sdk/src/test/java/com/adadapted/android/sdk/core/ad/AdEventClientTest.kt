package com.adadapted.android.sdk.core.ad

import com.adadapted.android.sdk.core.concurrency.TransporterCoroutineScope
import com.adadapted.android.sdk.core.event.AdEvent
import com.adadapted.android.sdk.core.event.AdEventTypes
import com.adadapted.android.sdk.core.event.EventClient
import com.adadapted.android.sdk.core.interfaces.EventClientListener
import com.adadapted.android.sdk.core.session.SessionClient
import com.adadapted.android.sdk.tools.MockData
import com.adadapted.android.sdk.tools.TestEventAdapter
import com.adadapted.android.sdk.tools.TestTransporter
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import junit.framework.Assert.assertNotNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AdEventClientTest {
    var testTransporter = UnconfinedTestDispatcher()
    val testTransporterScope: TransporterCoroutineScope = TestTransporter(testTransporter)
    var testAd = Ad("adId", "zoneId", "impId")

    @Before
    fun setup() {
        Dispatchers.setMain(testTransporter)
        SessionClient.createInstance(mock(), mock())
        EventClient.createInstance(TestEventAdapter, testTransporterScope)
        testAdEventClientFailSafes()
        EventClient.onSessionAvailable(MockData.session)
        EventClient.onAdsAvailable(MockData.session)
    }

    @Test
    fun createInstance() {
        assertNotNull(TestEventAdapter)
    }
    
    @Test
    fun addListenerAndTrackEventImpression() {
        val mockListener = mock<EventClientListener>()
        EventClient.addListener(mockListener)
        EventClient.trackImpression(testAd)
        verify(mockListener).onAdEventTracked(any())
    }

    @Test
    fun removeListener() {
        val mockListener = mock<EventClientListener>()
        EventClient.addListener(mockListener)
        EventClient.trackImpression(testAd)
        verify(mockListener).onAdEventTracked(any())

        EventClient.removeListener(mockListener)
        EventClient.trackImpression(testAd)
        verifyZeroInteractions(mockListener)
    }

    @Test
    fun trackInteraction() {
        val mockListener = TestAdEventClientListener()
        EventClient.addListener(mockListener)
        EventClient.trackInteraction(testAd)
        assert(mockListener.getTrackedEvent()?.eventType == AdEventTypes.INTERACTION)
    }

    @Test
    fun trackPopupBegin() {
        val mockListener = TestAdEventClientListener()
        EventClient.addListener(mockListener)
        EventClient.trackPopupBegin(testAd)
        assert(mockListener.getTrackedEvent()?.eventType == AdEventTypes.POPUP_BEGIN)
    }

    @Test
    fun onSessionInitFailed() {
        EventClient.onSessionInitFailed()

        val mockListener = mock<EventClientListener>()
        EventClient.addListener(mockListener)
        EventClient.trackImpression(testAd)
        verify(mockListener).onAdEventTracked(any())
    }

    private fun testAdEventClientFailSafes() {
        EventClient.onPublishEvents()
        EventClient.trackImpression(Ad())
    }
}

class TestAdEventClientListener: EventClientListener {
    private var trackedEvent: AdEvent? = null

    fun getTrackedEvent(): AdEvent? {
        return trackedEvent
    }

    override fun onAdEventTracked(event: AdEvent?) {
        trackedEvent = event
    }
}