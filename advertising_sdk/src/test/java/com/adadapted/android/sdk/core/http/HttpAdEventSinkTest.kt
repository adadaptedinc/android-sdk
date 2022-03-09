package com.adadapted.android.sdk.core.http

import com.adadapted.android.sdk.config.EventStrings
import com.adadapted.android.sdk.core.ad.AdEvent
import com.adadapted.android.sdk.core.concurrency.TransporterCoroutineScope
import com.adadapted.android.sdk.core.device.DeviceInfoClient
import com.adadapted.android.sdk.core.event.AppEventClient
import com.adadapted.android.sdk.core.event.TestAppEventSink
import com.adadapted.android.sdk.core.session.Session
import com.adadapted.android.sdk.core.session.SessionClient
import com.adadapted.android.sdk.ext.http.HttpAdEventSink
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

class HttpAdEventSinkTest {
    private var testTransporter = TestCoroutineDispatcher()
    private val testTransporterScope: TransporterCoroutineScope = TestTransporter(testTransporter)
    private var testAppEventSink = TestAppEventSink()
    private var testHttpRequestManager = TestHttpRequestManager()
    private lateinit var httpAdEventSink: HttpAdEventSink
    private var mockSession = Session("testId", true, true, 30, 1907245044, mutableMapOf())
    private var adEvents = setOf(AdEvent("adId", "adZoneId", "impressionId", AdEvent.Types.IMPRESSION, Date().time))

    @Before
    fun setup() {
        Dispatchers.setMain(testTransporter)
        DeviceInfoClient.createInstance(mock(),"", false, HashMap(), "", TestDeviceInfoExtractor(), testTransporterScope)
        SessionClient.createInstance(mock(), mock())
        AppEventClient.createInstance(testAppEventSink, testTransporterScope)
        testHttpRequestManager.createQueue(mock())
        httpAdEventSink = HttpAdEventSink("testBatchUrl", testHttpRequestManager)
    }

    @Test
    fun batchIsSent() {
        httpAdEventSink.sendBatch(mockSession, adEvents)
        assert(testHttpRequestManager.queueWasCreated)
        assertEquals("testBatchUrl", testHttpRequestManager.queuedRequest?.url)
    }

    @Test
    fun batchIsSentWithError() {
        testHttpRequestManager.shouldReturnError = true
        httpAdEventSink.sendBatch(mockSession, adEvents)
        AppEventClient.getInstance().onPublishEvents()
        assertEquals(EventStrings.AD_EVENT_TRACK_REQUEST_FAILED, testAppEventSink.testErrors.first().code)
        assert(testHttpRequestManager.queueWasCreated)
        assertEquals("testBatchUrl", testHttpRequestManager.queuedRequest?.url)
    }
}