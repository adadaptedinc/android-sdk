package com.adadapted.android.sdk.core.http

import com.adadapted.android.sdk.config.EventStrings
import com.adadapted.android.sdk.core.concurrency.TransporterCoroutineScope
import com.adadapted.android.sdk.core.device.DeviceInfo
import com.adadapted.android.sdk.core.device.DeviceInfoClient
import com.adadapted.android.sdk.core.event.AppError
import com.adadapted.android.sdk.core.event.AppEvent
import com.adadapted.android.sdk.core.event.AppEventClient
import com.adadapted.android.sdk.core.event.TestAppEventSink
import com.adadapted.android.sdk.core.session.SessionClient
import com.adadapted.android.sdk.ext.http.HttpAppEventSink
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
import kotlin.collections.HashMap

class HttpAppEventSinkTest {
    private var testTransporter = TestCoroutineDispatcher()
    private val testTransporterScope: TransporterCoroutineScope = TestTransporter(testTransporter)
    private var testAppEventSink = TestAppEventSink()
    private var testHttpRequestManager = TestHttpRequestManager()
    private lateinit var httpAppEventSink: HttpAppEventSink
    private var testEvents = setOf(AppEvent("testEventType", "testEventName", mapOf()))
    private var testErrors = setOf(AppError("testCode", "testMessage", mapOf()))

    @Before
    fun setup() {
        Dispatchers.setMain(testTransporter)
        DeviceInfoClient.createInstance(mock(),"", false, HashMap(), "", TestDeviceInfoExtractor(), testTransporterScope)
        SessionClient.createInstance(mock(), mock())
        AppEventClient.createInstance(testAppEventSink, testTransporterScope)
        testHttpRequestManager.createQueue(mock())
        httpAppEventSink = HttpAppEventSink("testEventUrl", "testErrorUrl", testHttpRequestManager)
        httpAppEventSink.generateWrappers(generateMockDeviceInfo())
    }

    @Test
    fun eventsPublished() {
        httpAppEventSink.publishEvent(testEvents)
        assert(testHttpRequestManager.queueWasCreated)
        assertEquals("testEventUrl", testHttpRequestManager.queuedRequest?.url)
    }

    @Test
    fun eventsPublishedWithError() {
        testHttpRequestManager.shouldReturnError = true
        httpAppEventSink.publishEvent(testEvents)
        AppEventClient.getInstance().onPublishEvents()
        assertEquals(EventStrings.APP_EVENT_REQUEST_FAILED, testAppEventSink.testErrors.first().code)
        assert(testHttpRequestManager.queueWasCreated)
        assertEquals("testEventUrl", testHttpRequestManager.queuedRequest?.url)
    }

    @Test
    fun errorsPublished() {
        httpAppEventSink.publishError(testErrors)
        assert(testHttpRequestManager.queueWasCreated)
        assertEquals("testErrorUrl", testHttpRequestManager.queuedRequest?.url)
    }

    @Test
    fun errorsPublishedWithError() {
        testHttpRequestManager.shouldReturnError = true
        httpAppEventSink.publishError(testErrors)
        assert(testHttpRequestManager.queueWasCreated)
        assertEquals("testErrorUrl", testHttpRequestManager.queuedRequest?.url)
    }


    companion object {
        fun generateMockDeviceInfo(): DeviceInfo {
            val deviceInfo = DeviceInfo()
            deviceInfo.device = "testDevice"
            deviceInfo.appId = "testAppId"
            deviceInfo.bundleId = "testBundleId"
            deviceInfo.bundleVersion = "testBundleVersion"
            deviceInfo.carrier = "testCarrier"
            deviceInfo.density = 0
            deviceInfo.deviceUdid = "DeviceUdId"
            deviceInfo.device = "testDevice"
            deviceInfo.udid = "testUdid"
            deviceInfo.setAllowRetargeting(true)
            deviceInfo.osv = "testOsv"
            deviceInfo.dw = 0
            deviceInfo.dh = 0
            deviceInfo.timezone = "testTimeZone"
            deviceInfo.locale = "testLocale"
            deviceInfo.params = mapOf()
            return deviceInfo
        }
    }
}