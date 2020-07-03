package com.adadapted.android.sdk.core.http

import androidx.test.platform.app.InstrumentationRegistry
import com.adadapted.android.sdk.config.EventStrings
import com.adadapted.android.sdk.core.addit.AdditContent
import com.adadapted.android.sdk.core.addit.PayloadAdapter
import com.adadapted.android.sdk.core.addit.PayloadEvent
import com.adadapted.android.sdk.core.concurrency.TransporterCoroutineScope
import com.adadapted.android.sdk.core.device.DeviceInfoClient
import com.adadapted.android.sdk.core.device.DeviceInfoClientTest
import com.adadapted.android.sdk.core.event.AppEventClient
import com.adadapted.android.sdk.core.event.TestAppEventSink
import com.adadapted.android.sdk.core.session.SessionClient
import com.adadapted.android.sdk.ext.http.HttpPayloadAdapter
import com.adadapted.android.sdk.tools.TestHttpRequestManager
import com.adadapted.android.sdk.tools.TestTransporter
import com.nhaarman.mockitokotlin2.mock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.collections.HashMap

@RunWith(RobolectricTestRunner::class)
class HttpPayloadAdapterTest {
    private var testTransporter = TestCoroutineDispatcher()
    private val testTransporterScope: TransporterCoroutineScope = TestTransporter(testTransporter)
    private var testAppEventSink = TestAppEventSink()
    private var testHttpRequestManager = TestHttpRequestManager()
    private lateinit var testHttpPayloadAdapter: HttpPayloadAdapter

    @Before
    fun setup() {
        Dispatchers.setMain(testTransporter)
        DeviceInfoClient.createInstance(InstrumentationRegistry.getInstrumentation().targetContext,"", false, HashMap(), DeviceInfoClientTest.Companion::requestAdvertisingIdInfo, testTransporterScope)
        SessionClient.createInstance(mock(), mock())
        AppEventClient.createInstance(testAppEventSink, testTransporterScope)
        testHttpRequestManager.createQueue(mock())
        testHttpPayloadAdapter = HttpPayloadAdapter("pickupUrl", "trackUrl", testHttpRequestManager)
    }

    @Test
    fun pickupPayload() {
        testHttpPayloadAdapter.pickup(HttpAppEventSinkTest.generateMockDeviceInfo(), object: PayloadAdapter.Callback {
            override fun onSuccess(content: List<AdditContent>) {}
        })
        assert(testHttpRequestManager.queueWasCreated)
        assertEquals("pickupUrl", testHttpRequestManager.queuedRequest?.url)
    }

    @Test
    fun pickupPayloadWithError() {
        testHttpRequestManager.shouldReturnError = true
        testHttpPayloadAdapter.pickup(HttpAppEventSinkTest.generateMockDeviceInfo(), object: PayloadAdapter.Callback {
            override fun onSuccess(content: List<AdditContent>) {}
        })
        AppEventClient.getInstance().onPublishEvents()
        assertEquals(EventStrings.PAYLOAD_PICKUP_REQUEST_FAILED, testAppEventSink.testErrors.first().code)
        assert(testHttpRequestManager.queueWasCreated)
        assertEquals("pickupUrl", testHttpRequestManager.queuedRequest?.url)
    }

    @Test
    fun publishPayloadEvents() {
        testHttpPayloadAdapter.publishEvent(PayloadEvent("testPayloadId", "testStatus"))
        assert(testHttpRequestManager.queueWasCreated)
        assertEquals("trackUrl", testHttpRequestManager.queuedRequest?.url)
    }

    @Test
    fun publishPayloadEventsWithError() {
        testHttpRequestManager.shouldReturnError = true
        testHttpPayloadAdapter.publishEvent(PayloadEvent("testPayloadId", "testStatus"))
        AppEventClient.getInstance().onPublishEvents()
        assertEquals(EventStrings.PAYLOAD_EVENT_REQUEST_FAILED, testAppEventSink.testErrors.first().code)
        assert(testHttpRequestManager.queueWasCreated)
        assertEquals("trackUrl", testHttpRequestManager.queuedRequest?.url)
    }
}