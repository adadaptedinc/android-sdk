package com.adadapted.android.sdk.core.session

import com.adadapted.android.sdk.core.concurrency.TransporterCoroutineScope
import com.adadapted.android.sdk.core.device.DeviceInfo
import com.adadapted.android.sdk.tools.TestTransporter
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import junit.framework.Assert.assertNotNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test

class SessionClientTest {

    var testTransporter = TestCoroutineDispatcher()
    val testTransporterScope: TransporterCoroutineScope = TestTransporter(testTransporter)
    var testSessionClient = SessionClient
    var mockSessionAdapter = mock<SessionAdapter>()

    @Before
    fun setup() {
        Dispatchers.setMain(testTransporter)
        whenever(mockSessionAdapter.sendInit(any(), any())).then {}
        whenever(mockSessionAdapter.sendRefreshAds(any(), any())).then {}

        testSessionClient.createInstance(mockSessionAdapter, testTransporterScope)
        testSessionClient.getInstance().onSessionInitialized(SessionTest().buildTestSession())
    }

    @Test
    fun createInstance() {
        assertNotNull(testSessionClient)
    }

    @Test
    fun start() {
        // Needs DeviceInfoClient Refactor
    }

    @Test
    fun addListener() {
        val mockListener = mock<SessionListener>()
        testSessionClient.getInstance().addListener(mockListener)
        verify(mockListener).onSessionAvailable(any())
    }

    @Test
    fun removeListener() {
        val testListener = TestSessionClientListener()
        testSessionClient.getInstance().addListener(testListener)
        assertNotNull(testListener.getTrackedSession())

        testSessionClient.getInstance().removeListener(testListener)
        testSessionClient.getInstance().onNewAdsLoaded(Session(DeviceInfo()))
        assert(testListener.getTrackedSession()?.id != "AdsAvailable")
    }

    @Test
    fun addPresenter() {
        val mockListener = mock<SessionListener>()
        testSessionClient.getInstance().addPresenter(mockListener)
        verify(mockListener).onSessionAvailable(any())
    }

    @Test
    fun removePresenter() {
        val testListener = TestSessionClientListener()
        testSessionClient.getInstance().addPresenter(testListener)
        assertNotNull(testListener.getTrackedSession())

        testSessionClient.getInstance().removePresenter(testListener)
        testSessionClient.getInstance().onNewAdsLoaded(Session(DeviceInfo()))
        assert(testListener.getTrackedSession()?.id != "AdsAvailable")
    }

    @Test
    fun onSessionInitialized() {
        val testListener = TestSessionClientListener()
        testSessionClient.getInstance().getSession(testListener)
        assert(testListener.getTrackedSession()?.id == "SessionAvailable")
    }

    @Test
    fun onSessionInitializeFailed() {
        // Needs DeviceInfoClient Refactor
    }

    @Test
    fun onNewAdsLoaded() {
        val testListener = TestSessionClientListener()
        testSessionClient.getInstance().addListener(testListener)
        testSessionClient.getInstance().onNewAdsLoaded(Session(DeviceInfo()))
        assert(testListener.getTrackedSession()?.id == "AdsAvailable")
    }

    @Test
    fun onNewAdsLoadFailed() {
        // Needs DeviceInfoClient Refactor
    }
}

class TestSessionClientListener: SessionListener() {
    private var trackedSession: Session? = null

    fun getTrackedSession(): Session? {
        return trackedSession
    }

    override fun onPublishEvents() {
        trackedSession = Session(DeviceInfo.empty(),"EventsPublished")
    }

    override fun onSessionAvailable(session: Session) {
        trackedSession = Session(DeviceInfo.empty(),"SessionAvailable")
    }

    override fun onAdsAvailable(session: Session) {
        trackedSession = Session(DeviceInfo.empty(),"AdsAvailable")
    }

    override fun onSessionInitFailed() {
        trackedSession = Session(DeviceInfo.empty(),"SessionFailedId")
    }
}