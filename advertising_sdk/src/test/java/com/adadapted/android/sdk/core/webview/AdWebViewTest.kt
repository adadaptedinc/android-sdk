package com.adadapted.android.sdk.core.webview

import android.view.MotionEvent
import androidx.test.platform.app.InstrumentationRegistry
import com.adadapted.android.sdk.core.ad.Ad
import com.adadapted.android.sdk.core.view.AdWebView
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

//@RunWith(RobolectricTestRunner::class)
//class AdWebViewTest {
//    private lateinit var testAdWebView: AdWebView
//    private var testAdWebViewListener = TestAdWebViewListener()
//    private var testContext = InstrumentationRegistry.getInstrumentation().targetContext
//
//    @Before
//    fun setup() {
//        testAdWebView = AdWebView(testContext, testAdWebViewListener)
//    }
//
//    @Test
//    fun adClickedTest() {
//        val testAd = Ad("TestAdId", url = "http://www.example.com")
//        testAdWebView.loadAd(testAd)
//        testAdWebView.dispatchTouchEvent(MotionEvent.obtain(1, 1, MotionEvent.ACTION_UP, 0f, 0f, 0))
//        assert(testAdWebViewListener.clickedAd)
//    }
//
//    @Test
//    fun loadBlankAdTest() {
//        testAdWebView.loadBlank()
//        assert(testAdWebViewListener.blankAdLoaded)
//    }
//}
//
//class TestAdWebViewListener: AdWebView.Listener {
//    var loadedAd = false
//    var clickedAd = false
//    var adFailed = false
//    var blankAdLoaded = false
//
//    override fun onAdLoadedInWebView(ad: Ad) {
//        loadedAd = true
//    }
//
//    override fun onAdLoadInWebViewFailed() {
//        adFailed = true
//    }
//
//    override fun onAdInWebViewClicked(ad: Ad) {
//        clickedAd = true
//    }
//
//    override fun onBlankAdInWebViewLoaded() {
//        blankAdLoaded = true
//    }
//}