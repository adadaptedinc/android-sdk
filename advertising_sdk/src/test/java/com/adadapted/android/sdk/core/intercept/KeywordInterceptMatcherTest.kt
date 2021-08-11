package com.adadapted.android.sdk.core.intercept

import com.adadapted.android.sdk.core.concurrency.TransporterCoroutineScope
import com.adadapted.android.sdk.core.device.DeviceInfoClient
import com.adadapted.android.sdk.core.event.AppEventClient
import com.adadapted.android.sdk.core.event.TestAppEventSink
import com.adadapted.android.sdk.core.session.Session
import com.adadapted.android.sdk.core.session.SessionClient
import com.adadapted.android.sdk.tools.TestDeviceInfoExtractor
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

class KeywordInterceptMatcherTest {
    private var testTransporter = TestCoroutineDispatcher()
    private val testTransporterScope: TransporterCoroutineScope = TestTransporter(testTransporter)
    private var testAppEventSink = TestAppEventSink()
    private var testInterceptAdapter = TestInterceptAdapter()
    private var mockSession = Session("testId", willServeAds = true, hasAds = true, refreshTime = 30, expiration = Date().time, zones = mutableMapOf())

    @Before
    fun setup() {
        Dispatchers.setMain(testTransporter)
        DeviceInfoClient.createInstance(mock(),"", false, HashMap(), TestDeviceInfoExtractor(), testTransporterScope)
        SessionClient.createInstance(mock(), testTransporterScope)
        AppEventClient.createInstance(testAppEventSink, testTransporterScope)
        val testIntercept = Intercept("test_searchId", 5, 3, listOf(
                Term("testTermId", "testTerm", "replacementTerm", "testIcon", "testTagLine", 1),
                Term("testTermTwoId", "testTermTwo", "replacementTermTwo", "testIcon", "testTagLine", 2)))
        testInterceptAdapter.testIntercept = testIntercept
        InterceptClient.createInstance(testInterceptAdapter, testTransporterScope)
        InterceptClient.getInstance().onSessionAvailable(mockSession)
        KeywordInterceptMatcher.match("INIT")
        SessionClient.getInstance().onSessionInitialized(Session("newSessionId", willServeAds = true, hasAds = true, refreshTime = 30, expiration = Date().time, zones = mutableMapOf()))
        clearEvents()
    }

    @Test
    fun interceptMatches() {
        KeywordInterceptMatcher.match("tes")
        InterceptClient.getInstance().onPublishEvents()
        assertEquals(InterceptEvent.MATCHED, testInterceptAdapter.testEvents.first().event)
    }

    @Test
    fun interceptDoesNotMatch() {
        KeywordInterceptMatcher.match("oxo")
        InterceptClient.getInstance().onPublishEvents()
        assertEquals(InterceptEvent.NOT_MATCHED, testInterceptAdapter.testEvents.first().event)
    }

    @Test
    fun sessionIsNotAvailable() {
        SessionClient.getInstance().onSessionInitialized(Session("", willServeAds = true, hasAds = true, refreshTime = 30, expiration = Date().time, zones = mutableMapOf()))
        KeywordInterceptMatcher.match("tes")
        InterceptClient.getInstance().onPublishEvents()
        AppEventClient.getInstance().onPublishEvents()
        assertEquals(InterceptEvent.MATCHED, testInterceptAdapter.testEvents.first().event)
    }

    @Test
    fun adIsAvailable() {
        SessionClient.getInstance().onNewAdsLoaded(Session("newSessionId", willServeAds = true, hasAds = true, refreshTime = 30, expiration = Date().time, zones = mutableMapOf()))
        KeywordInterceptMatcher.match("tes")
        InterceptClient.getInstance().onPublishEvents()
        AppEventClient.getInstance().onPublishEvents()
        assertEquals(InterceptEvent.MATCHED, testInterceptAdapter.testEvents.first().event)
    }

    private fun clearEvents() {
        testInterceptAdapter.testEvents.clear()
    }
}