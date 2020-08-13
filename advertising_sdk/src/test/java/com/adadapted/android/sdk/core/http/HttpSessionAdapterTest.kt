package com.adadapted.android.sdk.core.http

import com.adadapted.android.sdk.config.EventStrings
import com.adadapted.android.sdk.core.concurrency.TransporterCoroutineScope
import com.adadapted.android.sdk.core.device.DeviceInfo
import com.adadapted.android.sdk.core.device.DeviceInfoClient
import com.adadapted.android.sdk.core.event.AppEventClient
import com.adadapted.android.sdk.core.event.TestAppEventSink
import com.adadapted.android.sdk.core.session.Session
import com.adadapted.android.sdk.core.session.SessionAdapter
import com.adadapted.android.sdk.core.session.SessionClient
import com.adadapted.android.sdk.ext.http.HttpSessionAdapter
import com.adadapted.android.sdk.ext.json.JsonSessionBuilder
import com.adadapted.android.sdk.tools.TestDeviceInfoExtractor
import com.adadapted.android.sdk.tools.TestHttpRequestManager
import com.adadapted.android.sdk.tools.TestTransporter
import com.nhaarman.mockitokotlin2.mock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.util.Date
import kotlin.collections.HashMap

class HttpSessionAdapterTest {
    private var testTransporter = TestCoroutineDispatcher()
    private val testTransporterScope: TransporterCoroutineScope = TestTransporter(testTransporter)
    private var testAppEventSink = TestAppEventSink()
    private var testHttpRequestManager = TestHttpRequestManager()
    private lateinit var testHttpSessionAdapter: HttpSessionAdapter
    private var mockSession = Session(
            DeviceInfo().apply { appId = "testAppId" }.apply { udid = "testUdId" },
            "testSessionId", true, true, 30, Date(1907245044), mutableMapOf())

    @Before
    fun setup() {
        Dispatchers.setMain(testTransporter)
        DeviceInfoClient.createInstance(mock(),"", false, HashMap(), TestDeviceInfoExtractor(), testTransporterScope)
        SessionClient.createInstance(mock(), mock())
        AppEventClient.createInstance(testAppEventSink, testTransporterScope)
        testHttpRequestManager.createQueue(mock())
        testHttpSessionAdapter = HttpSessionAdapter("initUrl",  "refreshUrl", JsonSessionBuilder(HttpAppEventSinkTest.generateMockDeviceInfo()), testHttpRequestManager)
    }

    @Test
    fun initIsSent() {
        val testListener = TestSessionInitListener()
        testHttpSessionAdapter.sendInit(HttpAppEventSinkTest.generateMockDeviceInfo(), testListener)
        assert(testHttpRequestManager.queueWasCreated)
        assertEquals("initUrl", testHttpRequestManager.queuedRequest?.url)
    }

    @Test
    fun initIsSentWithErrors() {
        testHttpRequestManager.shouldReturnError = true
        val testListener = TestSessionInitListener()
        testHttpSessionAdapter.sendInit(HttpAppEventSinkTest.generateMockDeviceInfo(), testListener)
        AppEventClient.getInstance().onPublishEvents()
        assertEquals(EventStrings.SESSION_REQUEST_FAILED, testAppEventSink.testErrors.first().code)
        assert(testHttpRequestManager.queueWasCreated)
        assertEquals("initUrl", testHttpRequestManager.queuedRequest?.url)
    }

    @Test
    fun refreshedAdsSent() {
        val testInitListener = TestSessionInitListener()
        val testListener = TestAdGetListener()
        testHttpSessionAdapter.sendInit(HttpAppEventSinkTest.generateMockDeviceInfo(), testInitListener)
        testHttpSessionAdapter.sendRefreshAds(mockSession, testListener)
        assert(testHttpRequestManager.queueWasCreated)
        assertEquals("refreshUrl?aid=testAppId&uid=testUdId&sid=testSessionId&sdk=2.2.1", testHttpRequestManager.queuedRequest?.url)
    }

    @Test
    fun refreshedAdsSentWithErrors() {
        testHttpRequestManager.shouldReturnError = true
        val testListener = TestAdGetListener()
        testHttpSessionAdapter.sendRefreshAds(mockSession, testListener)
        AppEventClient.getInstance().onPublishEvents()
        assertEquals(EventStrings.AD_GET_REQUEST_FAILED, testAppEventSink.testErrors.first().code)
        assert(testHttpRequestManager.queueWasCreated)
        assertEquals("refreshUrl?aid=testAppId&uid=testUdId&sid=testSessionId&sdk=2.2.1", testHttpRequestManager.queuedRequest?.url)
    }
}

class TestSessionInitListener: SessionAdapter.SessionInitListener {
    var initFailed = false
    var initializedSession: Session? = null

    override fun onSessionInitialized(session: Session) {
        initializedSession = session
    }

    override fun onSessionInitializeFailed() {
        initFailed = true
    }
}

class TestAdGetListener: SessionAdapter.AdGetListener {
    var adsFailed = false
    var adsLoadedSession: Session? = null


    override fun onNewAdsLoaded(session: Session) {
        adsLoadedSession = session
    }

    override fun onNewAdsLoadFailed() {
        adsFailed = true
    }
}