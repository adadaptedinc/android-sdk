package com.adadapted.android.sdk.core.webview

//import android.content.Intent
//import android.view.KeyEvent
//import androidx.test.platform.app.InstrumentationRegistry
//import com.adadapted.android.sdk.core.ad.Ad
//import com.adadapted.android.sdk.core.concurrency.TransporterCoroutineScope
//import com.adadapted.android.sdk.core.device.DeviceInfoClient
//import com.adadapted.android.sdk.core.event.AdEventTypes
//import com.adadapted.android.sdk.core.event.EventClient
//import com.adadapted.android.sdk.core.payload.Payload
//import com.adadapted.android.sdk.core.session.SessionClient
//import com.adadapted.android.sdk.core.view.AaWebViewPopupActivity
//import com.adadapted.android.sdk.core.zone.TestAdEventClientListener
//import com.adadapted.android.sdk.tools.MockData
//import com.adadapted.android.sdk.tools.TestDeviceInfoExtractor
//import com.adadapted.android.sdk.tools.TestEventAdapter
//import com.adadapted.android.sdk.tools.TestTransporter
//import com.nhaarman.mockitokotlin2.mock
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.ExperimentalCoroutinesApi
//import kotlinx.coroutines.test.UnconfinedTestDispatcher
//import kotlinx.coroutines.test.setMain
//import kotlinx.serialization.json.Json
//import kotlinx.serialization.serializer
//import org.junit.Assert.assertEquals
//import org.junit.Before
//import org.junit.Test
//import org.junit.runner.RunWith
//import org.robolectric.Robolectric
//import org.robolectric.RobolectricTestRunner

//@OptIn(ExperimentalCoroutinesApi::class)
//@RunWith(RobolectricTestRunner::class)
//class AdWebViewPopupActivityTest {
//    private lateinit var testAaWebViewPopupActivity: AaWebViewPopupActivity
//    private var testContext = InstrumentationRegistry.getInstrumentation().targetContext
//    private var testTransporter = UnconfinedTestDispatcher()
//    private val testTransporterScope: TransporterCoroutineScope = TestTransporter(testTransporter)
//    private var testAd = Ad("TestAdId", "imp", "url", "type", "http://example.com", Payload(detailedListItems = listOf()), 5)
//
//    @Before
//    fun setup() {
//        Dispatchers.setMain(testTransporter)
//        DeviceInfoClient.createInstance("", false, HashMap(), "", TestDeviceInfoExtractor(), testTransporterScope)
//        SessionClient.createInstance(mock(), mock())
//        EventClient.createInstance(TestEventAdapter, testTransporterScope)
//        EventClient.onSessionAvailable(MockData.session)
//
//        val testIntent = Intent(testContext, AaWebViewPopupActivity::class.java)
//        testIntent.putExtra(AaWebViewPopupActivity::class.java.name + ".EXTRA_POPUP_AD", Json.encodeToString(
//            serializer(), testAd))
//        testIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//        testAaWebViewPopupActivity = Robolectric.buildActivity(AaWebViewPopupActivity::class.java, testIntent)
//                .create()
//                .resume()
//                .get()
//    }
//
//    @Test
//    fun testCreateActivity() {
//        val testIntent = testAaWebViewPopupActivity.createActivity(testContext, testAd)
//        assertEquals(Intent.FLAG_ACTIVITY_NEW_TASK, testIntent.flags)
//        assertEquals(AaWebViewPopupActivity::class.java.name, testIntent.component?.className)
//    }
//
//    @Test
//    fun testOnStart() {
//        val testAdEventListener = TestAdEventClientListener()
//        EventClient.addListener(testAdEventListener)
//        testAaWebViewPopupActivity.onStart()
//        assertEquals(AdEventTypes.POPUP_BEGIN, testAdEventListener.testAdEvent?.eventType)
//    }
//
//    @Test
//    fun testOnKeyDown() {
//        val result = testAaWebViewPopupActivity.onKeyDown(KeyEvent.KEYCODE_BACK, KeyEvent(KeyEvent.KEYCODE_BACK,0))
//        assert(result)
//    }
//}