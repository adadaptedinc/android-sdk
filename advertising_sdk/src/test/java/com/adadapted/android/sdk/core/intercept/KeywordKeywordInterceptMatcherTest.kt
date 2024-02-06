package com.adadapted.android.sdk.core.intercept

import com.adadapted.android.sdk.core.concurrency.TransporterCoroutineScope
import com.adadapted.android.sdk.core.device.DeviceInfoClient
import com.adadapted.android.sdk.core.event.EventClient
import com.adadapted.android.sdk.core.keyword.Intercept
import com.adadapted.android.sdk.core.keyword.InterceptClient
import com.adadapted.android.sdk.core.keyword.InterceptEvent
import com.adadapted.android.sdk.core.keyword.KeywordInterceptMatcher
import com.adadapted.android.sdk.core.keyword.Term
import com.adadapted.android.sdk.core.session.Session
import com.adadapted.android.sdk.core.session.SessionClient
import com.adadapted.android.sdk.tools.MockData
import com.adadapted.android.sdk.tools.TestDeviceInfoExtractor
import com.adadapted.android.sdk.tools.TestEventAdapter
import com.adadapted.android.sdk.tools.TestTransporter
import com.nhaarman.mockitokotlin2.mock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.util.Date
import kotlin.collections.HashMap
import kotlin.test.AfterTest

@OptIn(ExperimentalCoroutinesApi::class)
class KeywordKeywordInterceptMatcherTest {
    private var testTransporter = UnconfinedTestDispatcher()
    private val testTransporterScope: TransporterCoroutineScope = TestTransporter(testTransporter)
    private var testInterceptAdapter = TestInterceptAdapter()

    @Before
    fun setup() {
        Dispatchers.setMain(testTransporter)
        DeviceInfoClient.createInstance("", false, HashMap(), "", TestDeviceInfoExtractor(), testTransporterScope)
        SessionClient.createInstance(mock(), testTransporterScope)
        EventClient.createInstance(TestEventAdapter, testTransporterScope)
        EventClient.onSessionAvailable(MockData.session)
        val testIntercept = Intercept("test_searchId", 5, 3, listOf(
                Term("testTermId", "testTerm", "replacementTerm", "testIcon", "testTagLine", 1),
                Term("twoTermId", "twoTestTerm", "replacementTerm", "testIcon", "testTagLine", 1),
                Term("threeTermId", "threeTestTerm", "replacementTerm", "testIcon", "testTagLine", 1),
                Term("testTermTwoId", "testTermTwo", "replacementTermTwo", "testIcon", "testTagLine", 2)))
        testInterceptAdapter.testIntercept = testIntercept
        InterceptClient.createInstance(testInterceptAdapter, testTransporterScope)
        InterceptClient.getInstance().onSessionAvailable(MockData.session)
        KeywordInterceptMatcher.match("INIT")
        SessionClient.onSessionInitialized(Session("newSessionId", willServeAds = true, hasAds = true, refreshTime = 30, expiration = Date().time, zones = mutableMapOf()))
        clearEvents()
    }

    @AfterTest
    fun cleanup() {
        TestEventAdapter.cleanupEvents()
    }

    @Test
    fun interceptMatches() {
        KeywordInterceptMatcher.match("tes")
        InterceptClient.getInstance().onPublishEvents()
        assertEquals(InterceptEvent.MATCHED, testInterceptAdapter.testEvents.first{i -> i.userInput == "tes" }.event)
    }

    @Test
    fun interceptDoesNotMatch() {
        KeywordInterceptMatcher.match("oxo")
        InterceptClient.getInstance().onPublishEvents()
        assertEquals(InterceptEvent.NOT_MATCHED, testInterceptAdapter.testEvents.first{i -> i.userInput == "oxo" }.event)
    }

    @Test
    fun sessionIsNotAvailable() {
        SessionClient.onSessionInitialized(Session("", willServeAds = true, hasAds = true, refreshTime = 30, expiration = Date().time, zones = mutableMapOf()))
        KeywordInterceptMatcher.match("two")
        InterceptClient.getInstance().onPublishEvents()
        EventClient.onPublishEvents()
        assertEquals(InterceptEvent.MATCHED, testInterceptAdapter.testEvents.first{i -> i.userInput == "two" }.event)
    }

    @Test
    fun adIsAvailable() {
        SessionClient.onNewAdsLoaded(Session("newSessionId", willServeAds = true, hasAds = true, refreshTime = 30, expiration = Date().time, zones = mutableMapOf()))
        KeywordInterceptMatcher.match("thr")
        InterceptClient.getInstance().onPublishEvents()
        EventClient.onPublishEvents()
        assertEquals(InterceptEvent.MATCHED, testInterceptAdapter.testEvents.first{i -> i.userInput == "thr" }.event)
    }

    private fun clearEvents() {
        testInterceptAdapter.testEvents.clear()
    }
}