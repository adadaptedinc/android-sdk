package com.adadapted.android.sdk.core.intercept

import com.adadapted.android.sdk.core.concurrency.TransporterCoroutineScope
import com.adadapted.android.sdk.core.interfaces.InterceptListener
import com.adadapted.android.sdk.core.keyword.Intercept
import com.adadapted.android.sdk.core.keyword.InterceptAdapter
import com.adadapted.android.sdk.core.keyword.InterceptClient
import com.adadapted.android.sdk.core.keyword.InterceptEvent
import com.adadapted.android.sdk.core.session.Session
import com.adadapted.android.sdk.core.session.SessionClient
import com.adadapted.android.sdk.tools.MockData
import com.adadapted.android.sdk.tools.TestTransporter
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertNotNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class InterceptClientTest {
    private var testTransporter = UnconfinedTestDispatcher()
    private val testTransporterScope: TransporterCoroutineScope = TestTransporter(testTransporter)
    private var testInterceptClient = InterceptClient
    private var testInterceptAdapter = TestInterceptAdapter()
    private val testEvent = InterceptEvent("testId", "testTermId", "testTerm", "testInput")

    @Before
    fun setup() {
        Dispatchers.setMain(testTransporter)
        SessionClient.createInstance(mock(), mock())
        testInterceptClient.createInstance(testInterceptAdapter, testTransporterScope)
        testInterceptClient.getInstance().onSessionAvailable(MockData.session)
    }

    @Test
    fun createInstance() {
        assertNotNull(testInterceptClient.getInstance())
    }

    @Test
    fun initialize() {
        val mockListener = mock<InterceptListener>()
        testInterceptClient.getInstance().initialize(MockData.session, mockListener)
        verify(mockListener).onKeywordInterceptInitialized(any())
    }

    @Test
    fun trackMatched() {
        testInterceptClient.getInstance().trackMatched(testEvent.searchId, testEvent.termId, testEvent.term, testEvent.userInput)
        testInterceptClient.getInstance().onPublishEvents()

        assertEquals(InterceptEvent.MATCHED, testInterceptAdapter.testEvents.first().event)
    }

    @Test
    fun trackPresented() {
        testInterceptClient.getInstance().trackPresented(testEvent.searchId, testEvent.termId, testEvent.term, testEvent.userInput)
        testInterceptClient.getInstance().onPublishEvents()

        assertEquals(InterceptEvent.PRESENTED, testInterceptAdapter.testEvents.first().event)
    }

    @Test
    fun trackSelected() {
        testInterceptClient.getInstance().trackSelected(testEvent.searchId, testEvent.termId, testEvent.term, testEvent.userInput)
        testInterceptClient.getInstance().onPublishEvents()

        assertEquals(InterceptEvent.SELECTED, testInterceptAdapter.testEvents.first().event)
    }

    @Test
    fun trackNotMatched() {
        testInterceptClient.getInstance().trackNotMatched(testEvent.searchId, testEvent.userInput)
        testInterceptClient.getInstance().onPublishEvents()

        assertEquals(InterceptEvent.NOT_MATCHED, testInterceptAdapter.testEvents.first().event)
    }
}

class TestInterceptAdapter: InterceptAdapter {
    var testEvents = mutableSetOf<InterceptEvent>()
    var testIntercept = Intercept()
    override suspend fun retrieve(session: Session, listener: InterceptAdapter.Listener) {
        listener.onSuccess(testIntercept)
    }

    override suspend fun sendEvents(session: Session, events: MutableSet<InterceptEvent>) {
        testEvents = events
    }
}