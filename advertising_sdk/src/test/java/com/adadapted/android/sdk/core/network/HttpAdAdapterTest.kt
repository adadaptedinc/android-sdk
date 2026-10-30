package com.adadapted.android.sdk.core.network

import com.adadapted.android.sdk.constants.EventStrings
import com.adadapted.android.sdk.core.ad.AdZoneData
import com.adadapted.android.sdk.core.concurrency.TransporterCoroutineScope
import com.adadapted.android.sdk.core.device.DeviceInfoClient
import com.adadapted.android.sdk.core.event.EventClient
import com.adadapted.android.sdk.core.interfaces.ZoneAdListener
import com.adadapted.android.sdk.core.session.SessionClient
import com.adadapted.android.sdk.tools.TestDeviceInfoExtractor
import com.adadapted.android.sdk.tools.TestEventAdapter
import com.adadapted.android.sdk.tools.TestTransporter
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HttpAdAdapterTest {
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

    @Test
    fun aServerErrorWithAJsonBodyIsAFailedRequestNotANoFill() = runBlocking {
        val listener = RecordingZoneAdListener()

        adapterRespondingWith(HttpStatusCode.ServiceUnavailable, """{"error":"upstream timeout"}""")
            .requestAd("testZoneId", listener, "", "", "")
        EventClient.onPublishEvents()

        assertEquals("The request should have been reported failed", 1, listener.failures)
        assertNull("It should not have been reported as a loaded (empty) ad", listener.loaded)
        assertTrue(
            "The failure should be tracked as an ad request error",
            TestEventAdapter.testSdkErrors.any { it.code == EventStrings.AD_REQUEST_FAILED }
        )
    }

    @Test
    fun aNoFillIsStillALoadedEmptyAd() = runBlocking {
        val listener = RecordingZoneAdListener()

        adapterRespondingWith(HttpStatusCode.OK, """{"data":{"ad":{"refresh_time":300}},"success":true}""")
            .requestAd("testZoneId", listener, "", "", "")

        assertEquals("A no-fill should not be reported as a failure", 0, listener.failures)
        assertTrue("It should arrive as an empty Ad", listener.loaded?.ad?.isEmpty == true)
        assertEquals(300L, listener.loaded?.ad?.refreshTime)
    }

    private fun adapterRespondingWith(status: HttpStatusCode, body: String): HttpAdAdapter {
        val client = HttpClient(MockEngine {
            respond(body, status, headersOf(HttpHeaders.ContentType, "application/json"))
        }) {
            install(ContentNegotiation) {
                json(HttpConnector.jsonParser)
            }
            install(HttpRequestRetry) {
                delayMillis { 0L }
            }
        }
        return HttpAdAdapter("https://ads.test/zone", client)
    }

    private class RecordingZoneAdListener : ZoneAdListener {
        var loaded: AdZoneData? = null
        var failures = 0

        override fun onAdLoaded(adZoneData: AdZoneData) {
            loaded = adZoneData
        }

        override fun onAdLoadFailed() {
            failures++
        }
    }
}
