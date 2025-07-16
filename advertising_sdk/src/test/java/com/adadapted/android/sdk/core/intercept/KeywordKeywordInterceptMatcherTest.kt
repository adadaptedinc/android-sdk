package com.adadapted.android.sdk.core.intercept

import com.adadapted.android.sdk.core.concurrency.TransporterCoroutineScope
import com.adadapted.android.sdk.core.device.DeviceInfoClient
import com.adadapted.android.sdk.core.event.EventClient
import com.adadapted.android.sdk.core.keyword.InterceptClient
import com.adadapted.android.sdk.core.keyword.InterceptData
import com.adadapted.android.sdk.core.keyword.InterceptEvent
import com.adadapted.android.sdk.core.keyword.InterceptTerm
import com.adadapted.android.sdk.core.keyword.KeywordInterceptMatcher
import com.adadapted.android.sdk.core.session.SessionClient
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
        SessionClient.onStart(mock())
        EventClient.createInstance(TestEventAdapter, testTransporterScope)
        val testIntercept = InterceptData(
            "test_searchId", listOf(
                InterceptTerm("testTermId", "testTerm", "replacementTerm", 1),
                InterceptTerm("twoTermId", "twoTestTerm", "replacementTerm", 1),
                InterceptTerm(
                    "threeTermId",
                    "threeTestTerm",
                    "replacementTerm",
                    1
                ),
                InterceptTerm(
                    "testTermTwoId",
                    "testTermTwo",
                    "replacementTermTwo",
                    2
                )
            )
        )
        testInterceptAdapter.testIntercept = testIntercept
        InterceptClient.createInstance(testInterceptAdapter, testTransporterScope, true)
        KeywordInterceptMatcher.match("INIT")
        clearEvents()
    }

    @AfterTest
    fun cleanup() {
        TestEventAdapter.cleanupEvents()
    }

    @Test
    fun interceptMatches() {
        KeywordInterceptMatcher.match("tes")
        InterceptClient.performPublishEvents()
        assertEquals(InterceptEvent.MATCHED, testInterceptAdapter.testEvents.first{i -> i.userInput == "tes" }.event)
    }

    @Test
    fun interceptDoesNotMatch() {
        KeywordInterceptMatcher.match("oxo")
        InterceptClient.performPublishEvents()
        assertEquals(InterceptEvent.NOT_MATCHED, testInterceptAdapter.testEvents.first{i -> i.userInput == "oxo" }.event)
    }

    private fun clearEvents() {
        testInterceptAdapter.testEvents.clear()
    }
}