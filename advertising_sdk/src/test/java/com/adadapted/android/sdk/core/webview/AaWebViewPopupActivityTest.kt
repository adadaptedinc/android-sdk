package com.adadapted.android.sdk.core.webview

import android.content.Intent
import android.view.KeyEvent
import androidx.test.platform.app.InstrumentationRegistry
import com.adadapted.android.sdk.core.ad.Ad
import com.adadapted.android.sdk.core.ad.AdEvent
import com.adadapted.android.sdk.core.ad.AdEventClient
import com.adadapted.android.sdk.core.ad.Counter
import com.adadapted.android.sdk.core.concurrency.TransporterCoroutineScope
import com.adadapted.android.sdk.core.device.DeviceInfo
import com.adadapted.android.sdk.core.device.DeviceInfoClient
import com.adadapted.android.sdk.core.device.DeviceInfoClientTest
import com.adadapted.android.sdk.core.event.AppEventClient
import com.adadapted.android.sdk.core.session.Session
import com.adadapted.android.sdk.core.session.SessionClient
import com.adadapted.android.sdk.core.zone.TestAdEventClientListener
import com.adadapted.android.sdk.tools.TestAdEventSink
import com.adadapted.android.sdk.tools.TestTransporter
import com.adadapted.android.sdk.ui.activity.AaWebViewPopupActivity
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


@RunWith(RobolectricTestRunner::class)
class AaWebViewPopupActivityTest {

    private lateinit var testAaWebViewPopupActivity: AaWebViewPopupActivity
    private var mockImpressionIdCounter = mock<Counter>()
    private var mockAdEventSink = mock<TestAdEventSink>()
    private var testContext = InstrumentationRegistry.getInstrumentation().targetContext
    private var testTransporter = TestCoroutineDispatcher()
    private val testTransporterScope: TransporterCoroutineScope = TestTransporter(testTransporter)
    var mockSession = Session(DeviceInfo(), "testId", true, true, 30, Date(1907245044), mutableMapOf())
    private var testAd = Ad("TestAdId", "zone", "imp", "url", "type", "path", listOf(), 5, "html")

    @Before
    fun setup() {
        whenever(mockAdEventSink.sendBatch(any(), any())).then { }
        whenever(mockImpressionIdCounter.getCurrentCountFor(any())).thenReturn(1)

        Dispatchers.setMain(testTransporter)
        DeviceInfoClient.createInstance(testContext,"", false, HashMap(), DeviceInfoClientTest.Companion::requestAdvertisingIdInfo, testTransporterScope)
        SessionClient.createInstance(mock(), mock())
        AppEventClient.createInstance(mock(), testTransporterScope)
        AdEventClient.createInstance(mockAdEventSink, testTransporterScope, mockImpressionIdCounter)
        AdEventClient.getInstance().onSessionAvailable(mockSession)

        val testIntent = Intent(testContext, AaWebViewPopupActivity::class.java)
        testIntent.putExtra(AaWebViewPopupActivity::class.java.name + ".EXTRA_POPUP_AD", testAd)
        testIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        testAaWebViewPopupActivity = Robolectric.buildActivity<AaWebViewPopupActivity>(AaWebViewPopupActivity::class.java, testIntent)
                .create()
                .resume()
                .get()
    }

    @Test
    fun testCreateActivity() {
        val testIntent = testAaWebViewPopupActivity.createActivity(testContext, testAd)
        assertEquals(Intent.FLAG_ACTIVITY_NEW_TASK, testIntent.flags)
        assertEquals(AaWebViewPopupActivity::class.java.name, testIntent.component?.className)
    }

    @Test
    fun testOnStart() {
        val testAdEventListener = TestAdEventClientListener()
        AdEventClient.getInstance().addListener(testAdEventListener)
        testAaWebViewPopupActivity.onStart()
        assertEquals(AdEvent.Types.POPUP_BEGIN, testAdEventListener.testAdEvent?.eventType)
    }

    @Test
    fun testOnPause() {
        val testAdEventListener = TestAdEventClientListener()
        AdEventClient.getInstance().addListener(testAdEventListener)
        testAaWebViewPopupActivity.onPause()
        assertEquals(AdEvent.Types.POPUP_END, testAdEventListener.testAdEvent?.eventType)
    }

    @Test
    fun testOnKeyDown() {
        val result = testAaWebViewPopupActivity.onKeyDown(KeyEvent.KEYCODE_BACK, KeyEvent(KeyEvent.KEYCODE_BACK,0))
        assert(result)
    }
}