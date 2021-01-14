package com.adadapted.android.sdk.core.http

import com.adadapted.android.sdk.BuildConfig
import com.adadapted.android.sdk.config.EventStrings
import com.adadapted.android.sdk.core.concurrency.TransporterCoroutineScope
import com.adadapted.android.sdk.core.device.DeviceInfo
import com.adadapted.android.sdk.core.device.DeviceInfoClient
import com.adadapted.android.sdk.core.event.AppEventClient
import com.adadapted.android.sdk.core.event.TestAppEventSink
import com.adadapted.android.sdk.core.intercept.Intercept
import com.adadapted.android.sdk.core.intercept.InterceptAdapter
import com.adadapted.android.sdk.core.intercept.InterceptEvent
import com.adadapted.android.sdk.core.session.Session
import com.adadapted.android.sdk.core.session.SessionClient
import com.adadapted.android.sdk.ext.http.HttpInterceptAdapter
import com.adadapted.android.sdk.tools.TestDeviceInfoExtractor
import com.adadapted.android.sdk.tools.TestHttpRequestManager
import com.adadapted.android.sdk.tools.TestTransporter
import com.nhaarman.mockitokotlin2.mock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.util.Date

class HttpInterceptAdapterTest {
    private var testTransporter = TestCoroutineDispatcher()
    private val testTransporterScope: TransporterCoroutineScope = TestTransporter(testTransporter)
    private var testAppEventSink = TestAppEventSink()
    private var testHttpRequestManager = TestHttpRequestManager()
    private lateinit var httpInterceptAdapter: HttpInterceptAdapter
    private var mockSession = Session("testSessionId", true, true, 30, 1907245044, mutableMapOf())

    @Before
    fun setup() {
        mockSession.setDeviceInfo(DeviceInfo().apply { appId = "testAppId" }.apply { udid = "testUdId" })
        Dispatchers.setMain(testTransporter)
        DeviceInfoClient.createInstance(mock(),"", false, HashMap(), TestDeviceInfoExtractor(), testTransporterScope)
        SessionClient.createInstance(mock(), mock())
        AppEventClient.createInstance(testAppEventSink, testTransporterScope)
        testHttpRequestManager.createQueue(mock())
        httpInterceptAdapter = HttpInterceptAdapter("testInitUrl", "testEventUrl", testHttpRequestManager)
    }

    @After
    fun cleanup() {
        testHttpRequestManager.reset()
    }

    @Test
    fun retrieveIntercept() {
        httpInterceptAdapter.retrieve(mockSession, object: InterceptAdapter.Callback {
            override fun onSuccess(intercept: Intercept) {}
        })
        assert(testHttpRequestManager.queueWasCreated)
        assertEquals("testInitUrl?aid=testAppId&uid=testUdId&sid=testSessionId&sdk=" + BuildConfig.VERSION_NAME, testHttpRequestManager.queuedRequest?.url)
    }

    @Test
    fun retrieveInterceptWithError() {
        testHttpRequestManager.shouldReturnError = true
        httpInterceptAdapter.retrieve(mockSession, object: InterceptAdapter.Callback {
            override fun onSuccess(intercept: Intercept) {}
        })
        AppEventClient.getInstance().onPublishEvents()
        assert(testHttpRequestManager.queueWasCreated)
        assertEquals(EventStrings.KI_SESSION_REQUEST_FAILED, testAppEventSink.testErrors.first().code)
        assertEquals("testInitUrl", testAppEventSink.testErrors.first().params["url"])
        assertEquals("testInitUrl?aid=testAppId&uid=testUdId&sid=testSessionId&sdk=" + BuildConfig.VERSION_NAME, testHttpRequestManager.queuedRequest?.url)
    }

    @Test
    fun sendInterceptEvents() {
        val interceptEvents = mutableSetOf(InterceptEvent("searchId", "event", "input", "termId", "term"))
        httpInterceptAdapter.sendEvents(mockSession, interceptEvents)
        assert(testHttpRequestManager.queueWasCreated)
        assertEquals("testEventUrl", testHttpRequestManager.queuedRequest?.url)
    }

    @Test
    fun sendInterceptEventsWithError() {
        testHttpRequestManager.shouldReturnError = true
        val interceptEvents = mutableSetOf(InterceptEvent("searchId", "event", "input", "termId", "term"))
        httpInterceptAdapter.sendEvents(mockSession, interceptEvents)
        AppEventClient.getInstance().onPublishEvents()
        assertEquals(EventStrings.KI_EVENT_REQUEST_FAILED, testAppEventSink.testErrors.first().code)
        assert(testHttpRequestManager.queueWasCreated)
        assertEquals("testEventUrl", testHttpRequestManager.queuedRequest?.url)
    }
}