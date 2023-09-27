package com.adadapted.android.sdk.core.event

import com.adadapted.android.sdk.constants.EventStrings
import com.adadapted.android.sdk.core.concurrency.TransporterCoroutineScope
import com.adadapted.android.sdk.core.device.DeviceInfoClient
import com.adadapted.android.sdk.core.session.SessionClient
import com.adadapted.android.sdk.tools.MockData
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
        SessionClient.createInstance(mock(), mock())
        EventClient.createInstance(TestEventAdapter, testTransporterScope)
        EventClient.onSessionAvailable(MockData.session)
    }

    @AfterTest
    fun cleanup() {
        TestEventAdapter.cleanupEvents()
    }

    @Test
    fun trackAppEvent() {
        EventClient.trackSdkEvent("testTrackAppEvent")
        EventClient.onPublishEvents()
        assertEquals("sdk", TestEventAdapter.testSdkEvents.first().type)
        assertEquals("testTrackAppEvent", TestEventAdapter.testSdkEvents.first().name)
    }

    @Test
    fun trackSdkEvent() {
        EventClient.trackSdkEvent("testTrackSdkEvent", hashMapOf())
        EventClient.onPublishEvents()
        assertEquals("sdk", TestEventAdapter.testSdkEvents.first().type)
        assertEquals("testTrackSdkEvent", TestEventAdapter.testSdkEvents.first().name)
    }

    @Test
    fun trackError() {
        EventClient.trackSdkError("testErrorCode", "testTrackError", hashMapOf())
        EventClient.onPublishEvents()
        assertEquals("testErrorCode", TestEventAdapter.testSdkErrors.last().code)
        assertEquals("testTrackError", TestEventAdapter.testSdkErrors.last().message)
    }

    @Test
    fun onSessionExpired() {
        EventClient.onSessionExpired()
        EventClient.onPublishEvents()
        assertEquals("sdk", TestEventAdapter.testSdkEvents.first().type)
        assertEquals(EventStrings.EXPIRED_EVENT, TestEventAdapter.testSdkEvents.first().name)
    }
}