package com.adadapted.android.sdk.core.event

import androidx.test.platform.app.InstrumentationRegistry
import com.adadapted.android.sdk.core.ad.AdEvent
import com.adadapted.android.sdk.core.ad.AdEventClient
import com.adadapted.android.sdk.core.concurrency.TransporterCoroutineScope
import com.adadapted.android.sdk.core.device.DeviceInfo
import com.adadapted.android.sdk.core.device.DeviceInfoClient
import com.adadapted.android.sdk.core.device.DeviceInfoClientTest
import com.adadapted.android.sdk.core.session.Session
import com.adadapted.android.sdk.core.session.SessionClient
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
import java.util.Date
import kotlin.collections.HashMap

@RunWith(RobolectricTestRunner::class)
class SdkEventPublisherTest {

    private var testContext = InstrumentationRegistry.getInstrumentation().targetContext
    private var testTransporter = TestCoroutineDispatcher()
    private val testTransporterScope: TransporterCoroutineScope = TestTransporter(testTransporter)
    private var mockSession = Session(DeviceInfo(), "testId", true, true, 30, Date(1907245044), mutableMapOf())

    @Before
    fun setup() {
        Dispatchers.setMain(testTransporter)
        DeviceInfoClient.createInstance(testContext,"", false, HashMap(), DeviceInfoClientTest.Companion::requestAdvertisingIdInfo, testTransporterScope)
        SessionClient.createInstance(mock(), mock())
        AppEventClient.createInstance(mock(), mock())
        AdEventClient.createInstance(mock(), testTransporterScope, mock())
        AdEventClient.getInstance().onSessionAvailable(mockSession)
    }

    @Test
    fun addListenerAndPublishAdEventTracked() {
        val testListener = TestAaSdkEventListener()
        SdkEventPublisher.getInstance().setListener(testListener)
        SdkEventPublisher.getInstance().onAdEventTracked(AdEvent("adId", "adZoneId", "impressionId", AdEvent.Types.IMPRESSION))
        assertEquals(AdEvent.Types.IMPRESSION, testListener.resultEventType)
        assertEquals("adZoneId", testListener.resultZoneId)
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