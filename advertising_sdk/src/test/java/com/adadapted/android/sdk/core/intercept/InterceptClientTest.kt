package com.adadapted.android.sdk.core.intercept

import com.adadapted.android.sdk.core.concurrency.TransporterCoroutineScope
import com.adadapted.android.sdk.core.device.DeviceInfo
import com.adadapted.android.sdk.core.session.Session
import com.adadapted.android.sdk.core.session.SessionClient
import com.adadapted.android.sdk.tools.TestTransporter
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertNotNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test
import java.util.Date

class InterceptClientTest {

    private var testTransporter = TestCoroutineDispatcher()
    private val testTransporterScope: TransporterCoroutineScope = TestTransporter(testTransporter)
    private var testInterceptClient = InterceptClient
    private var testInterceptAdapter = TestInterceptAdapter()
    private val testEvent = InterceptEvent("testId", "testTermId", "testTerm", "testInput")
    private var mockSession = Session("testId", willServeAds = true, hasAds = true, refreshTime = 30, expiration = Date().time, zones = mutableMapOf())

    @Before
    fun setup() {
        Dispatchers.setMain(testTransporter)
        SessionClient.createInstance(mock(), mock())
        testInterceptClient.createInstance(testInterceptAdapter, testTransporterScope)
        testInterceptClient.getInstance().onSessionAvailable(mockSession)
    }

    @Test
    fun createInstance() {
        assertNotNull(testInterceptClient.getInstance())
    }

    @Test
    fun initialize() {
        val mockListener = mock<InterceptClient.Listener>()
        testInterceptClient.getInstance().initialize(mockSession, mockListener)
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

    override fun retrieve(session: Session, callback: InterceptAdapter.Callback) {
        callback.onSuccess(testIntercept)
    }

    override fun sendEvents(session: Session, events: MutableSet<InterceptEvent>) {
        testEvents = events
    }
}