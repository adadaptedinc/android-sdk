package com.adadapted.android.sdk.core.event

import com.adadapted.android.sdk.core.concurrency.TransporterCoroutineScope
import com.adadapted.android.sdk.core.concurrency.nowInSeconds
import com.adadapted.android.sdk.core.device.DeviceInfoClient
import com.adadapted.android.sdk.core.session.SessionClient
import com.adadapted.android.sdk.tools.TestDeviceInfoExtractor
import com.adadapted.android.sdk.tools.TestEventAdapter
import com.adadapted.android.sdk.tools.TestTransporter
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
        SessionClient.createOrResumeSession()
        EventClient.createInstance(TestEventAdapter, testTransporterScope)
        EventClient.onPublishEvents()
        TestEventAdapter.cleanupEvents()
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

    //Zone events carry no ad id and no impression id, so two mounts of the same zone inside the
    //same second are identical in every field the event has - the only thing telling them apart is
    //that both actually happened. A pending batch that dedupes on the event itself drops the second
    //one, and a zone rebuilt in place - a rotation, a fragment replace, a recycled AaZoneView -
    //mounts twice in that window, so the re-mount would never reach the server
    @Test
    fun aRemountInTheSameSecondIsNotDroppedFromTheBatch() {
        val zoneId = "remountedZoneId"
        waitForTheStartOfASecond()

        EventClient.trackZoneMounted(zoneId)
        EventClient.trackZoneUnmounted(zoneId)
        EventClient.trackZoneMounted(zoneId)
        EventClient.onPublishEvents()

        val mounts = TestEventAdapter.testAdEvents.filter {
            it.eventType == AdEventTypes.ZONE_MOUNTED && it.zoneId == zoneId
        }
        assertEquals("Both mounts happened, so both belong on the wire", 2, mounts.size)
    }

    //Events are stamped at one second granularity, so a test that needs two of them to collide has
    //to file them at the top of a second rather than trust that they land either side of a tick
    private fun waitForTheStartOfASecond() {
        val startingSecond = nowInSeconds()
        while (nowInSeconds() == startingSecond) {
            Thread.sleep(1)
        }
    }

    @Test
    fun trackError() {
        EventClient.trackSdkError("testErrorCode", "testTrackError", hashMapOf())
        EventClient.onPublishEvents()
        assertEquals("testErrorCode", TestEventAdapter.testSdkErrors.last().code)
        assertEquals("testTrackError", TestEventAdapter.testSdkErrors.last().message)
    }
}