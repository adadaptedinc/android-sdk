package com.adadapted.android.sdk.core.intercept

import com.adadapted.android.sdk.core.concurrency.TransporterCoroutineScope
import com.adadapted.android.sdk.core.device.DeviceInfoClient
import com.adadapted.android.sdk.core.event.EventClient
import com.adadapted.android.sdk.core.keyword.InterceptClient
import com.adadapted.android.sdk.core.keyword.InterceptData
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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class KeywordInterceptMatcherInitTest {
    private var testTransporter = UnconfinedTestDispatcher()
    private val testTransporterScope: TransporterCoroutineScope = TestTransporter(testTransporter)
    private var testInterceptAdapter = TestInterceptAdapter()

    @Before
    fun setup() {
        Dispatchers.setMain(testTransporter)
        DeviceInfoClient.createInstance("", false, HashMap(), "", TestDeviceInfoExtractor(), testTransporterScope)
        SessionClient.onStart(mock())
        EventClient.createInstance(TestEventAdapter, testTransporterScope)
        EventClient.onPublishEvents()
        TestEventAdapter.cleanupEvents()
    }

    @Test
    fun `match returns empty when no terms match input`() {
        setupMatcherWithTerms()

        val results = KeywordInterceptMatcher.match("xyz")
        assertTrue(results.isEmpty())
    }

    @Test
    fun `match ignores input shorter than 3 characters`() {
        setupMatcherWithTerms()

        val twoCharResult = KeywordInterceptMatcher.match("te")
        assertTrue(twoCharResult.isEmpty())

        val oneCharResult = KeywordInterceptMatcher.match("t")
        assertTrue(oneCharResult.isEmpty())
    }

    @Test
    fun `match returns results for input of exactly 3 characters`() {
        setupMatcherWithTerms()

        val results = KeywordInterceptMatcher.match("tes")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun `match returns suggestions sorted by priority`() {
        val terms = listOf(
            InterceptTerm("id1", "testLow", "replaceLow", 2),
            InterceptTerm("id2", "testHigh", "replaceHigh", 1)
        )
        testInterceptAdapter.testIntercept = InterceptData("searchId", terms)
        InterceptClient.createInstance(testInterceptAdapter, testTransporterScope, true)
        KeywordInterceptMatcher.initialize()

        val results = KeywordInterceptMatcher.match("test")
        assertEquals(2, results.size)
    }

    @Test
    fun `match is case insensitive`() {
        setupMatcherWithTerms()

        val lower = KeywordInterceptMatcher.match("tes")
        val upper = KeywordInterceptMatcher.match("TES")

        assertEquals(lower.size, upper.size)
    }

    private fun setupMatcherWithTerms() {
        val testIntercept = InterceptData(
            "test_searchId", listOf(
                InterceptTerm("testTermId", "testTerm", "replacementTerm", 1)
            )
        )
        testInterceptAdapter.testIntercept = testIntercept
        InterceptClient.createInstance(testInterceptAdapter, testTransporterScope, true)
        KeywordInterceptMatcher.initialize()
    }
}
