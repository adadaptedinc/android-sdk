package com.adadapted.android.sdk.core.adapter

import com.adadapted.android.sdk.core.intercept.InterceptClient
import com.adadapted.android.sdk.core.intercept.InterceptEvent
import com.adadapted.android.sdk.core.intercept.TestInterceptAdapter
import com.adadapted.android.sdk.tools.TestTransporter
import com.adadapted.android.sdk.ui.adapter.SuggestionTracker
import com.nhaarman.mockitokotlin2.mock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.util.Date

class SuggestionTrackerTest {

    private var testTransporter = TestCoroutineDispatcher()
    private val testTransporterScope: TransporterCoroutineScope = TestTransporter(testTransporter)
    private var testInterceptClient = InterceptClient
    private var testInterceptAdapter = TestInterceptAdapter()
    private val testEvent = InterceptEvent("testId", "testTermId", "testTerm", "testInput")
    private var mockSession = Session("testId", true, true, 30, Date().time, mutableMapOf())

    @Before
    fun setup() {
        Dispatchers.setMain(testTransporter)
        SessionClient.createInstance(mock(), mock())
        testInterceptClient.createInstance(testInterceptAdapter, testTransporterScope)
        testInterceptClient.getInstance().onSessionAvailable(mockSession)


    }

    @Test
    fun suggestionMatchedTest() {
        SuggestionTracker.suggestionMatched("testMatchId", "testTermId", "testTerm", "testReplacement", "testInput")
        testInterceptClient.getInstance().onPublishEvents()
        assertEquals(InterceptEvent.MATCHED, testInterceptAdapter.testEvents.first().event)
        assertEquals("testMatchId", testInterceptAdapter.testEvents.first().searchId)
    }

    @Test
    fun suggestionPresentedTest() {
        SuggestionTracker.suggestionMatched("testPresentedId", "testTermId", "testTerm", "testReplacement", "testInput")
        SuggestionTracker.suggestionPresented("testPresentedId", "testTermId", "testReplacement")
        testInterceptClient.getInstance().onPublishEvents()
        assert(testInterceptAdapter.testEvents.any { event -> event.event == InterceptEvent.PRESENTED })
        assertEquals("testPresentedId", testInterceptAdapter.testEvents.first().searchId)
    }

    @Test
    fun suggestionSelectedTest() {
        SuggestionTracker.suggestionMatched("testSelectedId", "testTermId", "testTerm", "testReplacement", "testInput")
        SuggestionTracker.suggestionSelected("testSelectedId", "testTermId", "testReplacement")
        testInterceptClient.getInstance().onPublishEvents()
        assert(testInterceptAdapter.testEvents.any { event -> event.event == InterceptEvent.SELECTED })
        assertEquals("testSelectedId", testInterceptAdapter.testEvents.first().searchId)
    }

    @Test
    fun suggestionNotMatchedTest() {
        SuggestionTracker.suggestionNotMatched("testNotMatchedId", "testInput")
        testInterceptClient.getInstance().onPublishEvents()
        assertEquals(InterceptEvent.NOT_MATCHED, testInterceptAdapter.testEvents.first().event)
        assertEquals("testNotMatchedId", testInterceptAdapter.testEvents.first().searchId)
    }
}