package com.adadapted.android.sdk.core.adapter

import com.adadapted.android.sdk.config.EventStrings
import com.adadapted.android.sdk.core.concurrency.TransporterCoroutineScope
import com.adadapted.android.sdk.core.device.DeviceInfo
import com.adadapted.android.sdk.core.device.DeviceInfoClient
import com.adadapted.android.sdk.core.event.AppEventClient
import com.adadapted.android.sdk.core.event.TestAppEventSink
import com.adadapted.android.sdk.core.intercept.Intercept
import com.adadapted.android.sdk.core.intercept.InterceptClient
import com.adadapted.android.sdk.core.intercept.InterceptEvent
import com.adadapted.android.sdk.core.intercept.Term
import com.adadapted.android.sdk.core.intercept.TestInterceptAdapter
import com.adadapted.android.sdk.core.session.Session
import com.adadapted.android.sdk.core.session.SessionClient
import com.adadapted.android.sdk.tools.TestDeviceInfoExtractor
import com.adadapted.android.sdk.tools.TestTransporter
import com.adadapted.android.sdk.ui.adapter.KeywordInterceptMatcher
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
    private lateinit var keywordInterceptMatcher: KeywordInterceptMatcher
    private var mockSession = Session(DeviceInfo(), "testId", willServeAds = true, hasAds = true, refreshTime = 30, expiresAt = Date(), zones = mutableMapOf())

    @Before
    fun setup() {
        Dispatchers.setMain(testTransporter)
        DeviceInfoClient.createInstance(mock(),"", false, HashMap(), TestDeviceInfoExtractor(), testTransporterScope)
        SessionClient.createInstance(mock(), mock())
        AppEventClient.createInstance(testAppEventSink, testTransporterScope)
        InterceptClient.createInstance(testInterceptAdapter, testTransporterScope)
        InterceptClient.getInstance().onSessionAvailable(mockSession)
        val testIntercept = Intercept("test_searchId", 5, 3, listOf(Term("testTermId", "testTerm", "replacementTerm", "testIcon", "testTagLine", 1)))
        keywordInterceptMatcher = KeywordInterceptMatcher()
        keywordInterceptMatcher.onKeywordInterceptInitialized(testIntercept)
    }

    @Test
    fun interceptMatches() {
        keywordInterceptMatcher.match("tes")
        InterceptClient.getInstance().onPublishEvents()
        assertEquals(InterceptEvent.MATCHED, testInterceptAdapter.testEvents.first().event)
    }

    @Test
    fun interceptMatchesContains() {
        keywordInterceptMatcher.match("ter")
        InterceptClient.getInstance().onPublishEvents()
        assertEquals(InterceptEvent.MATCHED, testInterceptAdapter.testEvents.first().event)
    }

    @Test
    fun interceptDoesNotMatch() {
        keywordInterceptMatcher.match("oxo")
        InterceptClient.getInstance().onPublishEvents()
        assertEquals(InterceptEvent.NOT_MATCHED, testInterceptAdapter.testEvents.first().event)
    }

    @Test
    fun sessionIsAvailable() {
        keywordInterceptMatcher.onSessionAvailable(Session(DeviceInfo(), "newSessionId", willServeAds = true, hasAds = true, refreshTime = 30, expiresAt = Date(), zones = mutableMapOf()))
        keywordInterceptMatcher.match("tes")
        InterceptClient.getInstance().onPublishEvents()
        AppEventClient.getInstance().onPublishEvents()
        assertEquals(InterceptEvent.NOT_MATCHED, testInterceptAdapter.testEvents.first().event)
        assertEquals(EventStrings.KI_INITIALIZED, testAppEventSink.testEvents.first().name)
    }

    @Test
    fun sessionIsNotAvailable() {
        keywordInterceptMatcher.onSessionAvailable(Session(DeviceInfo(), "", willServeAds = true, hasAds = true, refreshTime = 30, expiresAt = Date(), zones = mutableMapOf()))
        keywordInterceptMatcher.match("tes")
        InterceptClient.getInstance().onPublishEvents()
        AppEventClient.getInstance().onPublishEvents()
        assertEquals(InterceptEvent.MATCHED, testInterceptAdapter.testEvents.first().event)
    }

    @Test
    fun adIsAvailable() {
        keywordInterceptMatcher.onAdsAvailable(Session(DeviceInfo(), "newSessionId", willServeAds = true, hasAds = true, refreshTime = 30, expiresAt = Date(), zones = mutableMapOf()))
        keywordInterceptMatcher.match("tes")
        InterceptClient.getInstance().onPublishEvents()
        AppEventClient.getInstance().onPublishEvents()
        assertEquals(InterceptEvent.NOT_MATCHED, testInterceptAdapter.testEvents.first().event)
        assertEquals(EventStrings.KI_INITIALIZED, testAppEventSink.testEvents.first().name)
    }
}