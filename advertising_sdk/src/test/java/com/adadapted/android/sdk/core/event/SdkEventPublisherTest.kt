package com.adadapted.android.sdk.core.event

import android.os.Looper
import androidx.test.platform.app.InstrumentationRegistry
import com.adadapted.android.sdk.core.ad.AdEvent
import com.adadapted.android.sdk.core.ad.AdEventClient
import com.adadapted.android.sdk.core.concurrency.TransporterCoroutineScope
import com.adadapted.android.sdk.core.device.DeviceInfo
import com.adadapted.android.sdk.core.device.DeviceInfoClient
import com.adadapted.android.sdk.core.session.Session
import com.adadapted.android.sdk.core.session.SessionClient
import com.adadapted.android.sdk.tools.TestDeviceInfoExtractor
import com.adadapted.android.sdk.tools.TestTransporter
import com.adadapted.android.sdk.ui.messaging.AaSdkEventListener
import com.adadapted.android.sdk.ui.messaging.SdkEventPublisher
import com.nhaarman.mockitokotlin2.mock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import java.util.Date
import kotlin.collections.HashMap

@RunWith(RobolectricTestRunner::class)
class SdkEventPublisherTest {
    private var testTransporter = TestCoroutineDispatcher()
    private val testTransporterScope: TransporterCoroutineScope = TestTransporter(testTransporter)
    private var mockSession = Session("testId", true, true, 30, 1907245044, mutableMapOf())

    @Before
    fun setup() {
        Dispatchers.setMain(testTransporter)
        DeviceInfoClient.createInstance(mock(),"", false, HashMap(), TestDeviceInfoExtractor(), testTransporterScope)
        SessionClient.createInstance(mock(), mock())
        AppEventClient.createInstance(mock(), mock())
        AdEventClient.createInstance(mock(), testTransporterScope)
        AdEventClient.getInstance().onSessionAvailable(mockSession)
    }

    @Test
    fun addListenerAndPublishAdEventTracked() {
        val testListener = TestAaSdkEventListener()
        SdkEventPublisher.getInstance().setListener(testListener)
        SdkEventPublisher.getInstance().onAdEventTracked(AdEvent("adId", "adZoneId", "impressionId", AdEvent.Types.IMPRESSION))
        Shadows.shadowOf(Looper.getMainLooper()).idle()
        assertEquals("impression", testListener.resultEventType)
        assertEquals("adZoneId", testListener.resultZoneId)
    }

    @Test
    fun addListenerAndPublishAdEventInteractionTracked() {
        val testListener = TestAaSdkEventListener()
        SdkEventPublisher.getInstance().setListener(testListener)
        SdkEventPublisher.getInstance().onAdEventTracked(AdEvent("adId", "adZoneId", "impressionId", AdEvent.Types.INTERACTION))
        Shadows.shadowOf(Looper.getMainLooper()).idle()
        assertEquals("click", testListener.resultEventType)
        assertEquals("adZoneId", testListener.resultZoneId)
    }

    @Test
    fun addListenerAndPublishAdEventListenerNullNotTracked() {
        val testListener = TestAaSdkEventListener()
        SdkEventPublisher.getInstance().onAdEventTracked(AdEvent("adId", "adZoneId", "impressionId", AdEvent.Types.IMPRESSION))
        Shadows.shadowOf(Looper.getMainLooper()).idle()
        assertEquals("", testListener.resultEventType)
        assertEquals("", testListener.resultZoneId)
    }
    @Test
    fun addListenerAndPublishAdEventNullNotTracked() {
        val testListener = TestAaSdkEventListener()
        SdkEventPublisher.getInstance().setListener(testListener)
        SdkEventPublisher.getInstance().onAdEventTracked(null)
        Shadows.shadowOf(Looper.getMainLooper()).idle()
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