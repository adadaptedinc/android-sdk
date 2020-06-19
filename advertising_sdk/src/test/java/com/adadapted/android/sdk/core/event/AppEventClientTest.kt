package com.adadapted.android.sdk.core.event

import androidx.test.platform.app.InstrumentationRegistry
import com.adadapted.android.sdk.config.EventStrings
import com.adadapted.android.sdk.core.concurrency.TransporterCoroutineScope
import com.adadapted.android.sdk.core.device.DeviceInfo
import com.adadapted.android.sdk.core.device.DeviceInfoClient
import com.adadapted.android.sdk.core.device.DeviceInfoClientTest
import com.adadapted.android.sdk.core.session.SessionClient
import com.adadapted.android.sdk.tools.TestTransporter
import com.nhaarman.mockitokotlin2.mock
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertNotNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class AppEventClientTest {
    private var testTransporter = TestCoroutineDispatcher()
    private val testTransporterScope: TransporterCoroutineScope = TestTransporter(testTransporter)
    private val testAppEventClient = AppEventClient
    private var testAppEventSink = TestAppEventSink()

    @Before
    fun setup() {
        Dispatchers.setMain(testTransporter)
        DeviceInfoClient.createInstance(InstrumentationRegistry.getInstrumentation().targetContext,"", false, HashMap(), DeviceInfoClientTest.Companion::requestAdvertisingIdInfo, testTransporterScope)
        SessionClient.createInstance(mock(), mock())

        testAppEventClient.createInstance(testAppEventSink, testTransporterScope)
    }

    @Test
    fun createInstance() {
        assertNotNull(testAppEventClient)
        assertEquals("unknown robolectric", testAppEventSink.testDeviceInfo.device)
    }

    @Test
    fun trackAppEvent() {
        testAppEventClient.getInstance().trackAppEvent("testTrackAppEvent", hashMapOf())
        testAppEventClient.getInstance().onPublishEvents()
        assertEquals("app", testAppEventSink.testEvents.first().type)
        assertEquals("testTrackAppEvent", testAppEventSink.testEvents.first().name)
    }

    @Test
    fun trackSdkEvent() {
        testAppEventClient.getInstance().trackSdkEvent("testTrackSdkEvent", hashMapOf())
        testAppEventClient.getInstance().onPublishEvents()
        assertEquals("sdk", testAppEventSink.testEvents.first().type)
        assertEquals("testTrackSdkEvent", testAppEventSink.testEvents.first().name)
    }

    @Test
    fun trackError() {
        testAppEventClient.getInstance().trackError("testErrorCode", "testTrackError", hashMapOf())
        testAppEventClient.getInstance().onPublishEvents()
        assertEquals("testErrorCode", testAppEventSink.testErrors.first().code)
        assertEquals("testTrackError", testAppEventSink.testErrors.first().message)
    }

    @Test
    fun onSessionExpired() {
        testAppEventClient.getInstance().onSessionExpired()
        testAppEventClient.getInstance().onPublishEvents()
        assertEquals("sdk", testAppEventSink.testEvents.first().type)
        assertEquals(EventStrings.EXPIRED_EVENT, testAppEventSink.testEvents.first().name)
    }
}

class TestAppEventSink: AppEventSink {
    var testEvents = mutableSetOf<AppEvent>()
    var testErrors = mutableSetOf<AppError>()
    var testDeviceInfo = DeviceInfo()

    override fun publishError(errors: Set<AppError>) {
        testErrors = errors.toMutableSet()
    }

    override fun publishEvent(events: Set<AppEvent>) {
        testEvents = events.toMutableSet()
    }

    override fun generateWrappers(deviceInfo: DeviceInfo) {
        testDeviceInfo = deviceInfo
    }
}