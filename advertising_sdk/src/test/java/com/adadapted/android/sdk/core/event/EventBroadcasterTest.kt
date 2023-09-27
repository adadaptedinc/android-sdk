package com.adadapted.android.sdk.core.event

import com.adadapted.android.sdk.core.concurrency.TransporterCoroutineScope
import com.adadapted.android.sdk.core.device.DeviceInfoClient
import com.adadapted.android.sdk.core.interfaces.AaSdkEventListener
import com.adadapted.android.sdk.core.session.SessionClient
import com.adadapted.android.sdk.tools.MockData
import com.adadapted.android.sdk.tools.TestDeviceInfoExtractor
import com.adadapted.android.sdk.tools.TestEventAdapter
import com.adadapted.android.sdk.tools.TestTransporter
import com.nhaarman.mockitokotlin2.mock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.collections.HashMap
import kotlin.test.AfterTest

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class EventBroadcasterTest {
    private var testTransporter = UnconfinedTestDispatcher()
    private val testTransporterScope: TransporterCoroutineScope = TestTransporter(testTransporter)
    private val testListener = TestAaSdkEventListener()

    @Before
    fun setup() {
        Dispatchers.setMain(testTransporter)
        DeviceInfoClient.createInstance("", false, HashMap(), "", TestDeviceInfoExtractor(), testTransporterScope)
        SessionClient.createInstance(mock(), mock())
        EventClient.createInstance(TestEventAdapter, testTransporterScope)
        EventClient.onSessionAvailable(MockData.session)
        EventBroadcaster.setListener(testListener, testTransporterScope)
    }

    @After
    fun cleanup() {
        TestEventAdapter.cleanupEvents()
    }

    @AfterTest
    fun resetListener() {
        testListener.resultEventType = ""
        testListener.resultZoneId = ""
    }

    @Test
    fun addListenerAndPublishAdEventTracked() {
        EventBroadcaster.onAdEventTracked(AdEvent("adId", "adZoneId", "impressionId", AdEventTypes.IMPRESSION))
        assertEquals("impression", testListener.resultEventType)
        assertEquals("adZoneId", testListener.resultZoneId)
    }

    @Test
    fun addListenerAndPublishAdEventInteractionTracked() {
        EventBroadcaster.onAdEventTracked(AdEvent("adId", "adZoneId", "impressionId", AdEventTypes.INTERACTION))
        assertEquals("interaction", testListener.resultEventType)
        assertEquals("adZoneId", testListener.resultZoneId)
    }

    @Test
    fun addListenerAndPublishAdEventNullNotTracked() {
        EventBroadcaster.onAdEventTracked(null)
        assertEquals("", testListener.resultEventType)
        assertEquals("", testListener.resultZoneId)
    }
}

class TestAaSdkEventListener: AaSdkEventListener {
    var resultZoneId = ""
    var resultEventType = ""

    override fun onNextAdEvent(zoneId: String, eventType: String) {
        resultZoneId = zoneId
        resultEventType = eventType
    }
}