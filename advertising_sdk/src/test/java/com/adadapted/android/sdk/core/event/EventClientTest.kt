package com.adadapted.android.sdk.core.event

import com.adadapted.android.sdk.core.concurrency.TransporterCoroutineScope
import com.adadapted.android.sdk.core.device.DeviceInfoClient
import com.adadapted.android.sdk.core.session.SessionClient
import com.adadapted.android.sdk.tools.TestDeviceInfoExtractor
import com.adadapted.android.sdk.tools.TestEventAdapter
import com.adadapted.android.sdk.tools.TestTransporter
import com.nhaarman.mockitokotlin2.mock
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test
import kotlin.test.AfterTest

@OptIn(ExperimentalCoroutinesApi::class)
class EventClientTest {
    private var testTransporter = UnconfinedTestDispatcher()
    private val testTransporterScope: TransporterCoroutineScope = TestTransporter(testTransporter)

    @Before
    fun setup() {
        Dispatchers.setMain(testTransporter)
        DeviceInfoClient.createInstance("", false, HashMap(), "", TestDeviceInfoExtractor(), testTransporterScope)
        SessionClient.onStart(mock())
        EventClient.createInstance(TestEventAdapter, testTransporterScope)
    }

    @AfterTest
    fun cleanup() {
        TestEventAdapter.cleanupEvents()
    }

    @Test
    fun trackAppEvent() {
        EventClient.trackSdkEvent("testTrackAppEvent")
        EventClient.onPublishEvents()
        assert(TestEventAdapter.testSdkEvents.any { event -> event.type == "sdk" })
        var event = TestEventAdapter.testSdkEvents.first { event -> event.name == "testTrackAppEvent" }
        assertEquals("testTrackAppEvent", event.name)
    }

    @Test
    fun trackSdkEvent() {
        EventClient.trackSdkEvent("testTrackSdkEvent", hashMapOf())
        EventClient.onPublishEvents()
        assert(TestEventAdapter.testSdkEvents.any { event -> event.type == "sdk" })
        var event = TestEventAdapter.testSdkEvents.first { event -> event.name == "testTrackSdkEvent" }
        assertEquals("testTrackSdkEvent", event.name)
    }

    @Test
    fun trackError() {
        EventClient.trackSdkError("testErrorCode", "testTrackError", hashMapOf())
        EventClient.onPublishEvents()
        assertEquals("testErrorCode", TestEventAdapter.testSdkErrors.last().code)
        assertEquals("testTrackError", TestEventAdapter.testSdkErrors.last().message)
    }
}