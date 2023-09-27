package com.adadapted.android.sdk.core.session

import com.adadapted.android.sdk.core.concurrency.TransporterCoroutineScope
import com.adadapted.android.sdk.core.device.DeviceInfo
import com.adadapted.android.sdk.core.interfaces.AdGetListener
import com.adadapted.android.sdk.core.interfaces.SessionAdapter
import com.adadapted.android.sdk.core.interfaces.SessionInitListener
import com.adadapted.android.sdk.tools.TestTransporter
import junit.framework.Assert.assertNotNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SessionClientTest {
    var testTransporter = UnconfinedTestDispatcher()
    val testTransporterScope: TransporterCoroutineScope = TestTransporter(testTransporter)
    private var testSessionClient = SessionClient
    private var mockSessionAdapter = TestSessionAdapter()
    private lateinit var testListener: SessionListener
    private var onSessionAvailHit = false

    @Before
    fun setup() {
        Dispatchers.setMain(testTransporter)
        testSessionClient.createInstance(mockSessionAdapter, testTransporterScope)
        testSessionClient.onSessionInitialized(SessionTest().buildTestSession())
        testListener = object : SessionListener {
            override fun onSessionAvailable(session: Session) {
                onSessionAvailHit = true
            }

            override fun onAdsAvailable(session: Session) {
            }

            override fun onSessionInitFailed() {
            }
        }
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun createInstance() {
        assertNotNull(testSessionClient)
    }

    @Test
    fun start() {
        val testListener = TestSessionClientListener()
        testSessionClient.start(testListener)
        assert(testListener.getTrackedSession()?.id == "SessionAvailable")
    }

    @Test
    fun addListener() {
        testSessionClient.addListener(testListener)
        assert(onSessionAvailHit)
    }

    @Test
    fun removeListener() {
        val testListener = TestSessionClientListener()
        testSessionClient.addListener(testListener)
        assertNotNull(testListener.getTrackedSession())

        testSessionClient.removeListener(testListener)
        testSessionClient.onNewAdsLoaded(Session())
        assert(testListener.getTrackedSession()?.id != "AdsAvailable")
    }

    @Test
    fun addRemovePresenter() {
        val testListener = TestSessionClientListener()
        testSessionClient.addPresenter(testListener)
        assertNotNull(testListener.getTrackedSession())

        testSessionClient.removePresenter(testListener)
        testSessionClient.onNewAdsLoaded(Session())
        assert(testListener.getTrackedSession()?.id != "AdsAvailable")
    }

    @Test
    fun onSessionInitializeFailed() {
        val testListener = TestSessionClientListener()
        testSessionClient.start(testListener)
        testSessionClient.onSessionInitializeFailed()
        assert(testListener.getTrackedSession()?.id == "SessionFailed")
    }

    @Test
    fun onNewAdsLoaded() {
        val testListener = TestSessionClientListener()
        testSessionClient.addListener(testListener)
        testSessionClient.onNewAdsLoaded(Session())
        assert(testListener.getTrackedSession()?.id == "AdsAvailable")
    }

    @Test
    fun onNewAdsLoadFailed() {
        val testListener = TestSessionClientListener()
        testSessionClient.start(testListener)
        testSessionClient.onNewAdsLoadFailed()
        assert(testListener.getTrackedSession()?.id == "AdsAvailable")
    }
}

class TestSessionClientListener: SessionListener {
    private var trackedSession: Session? = null

    fun getTrackedSession(): Session? {
        return trackedSession
    }

    override fun onPublishEvents() {
        trackedSession = Session("EventsPublished")
    }

    override fun onSessionAvailable(session: Session) {
        trackedSession = Session("SessionAvailable")
    }

    override fun onAdsAvailable(session: Session) {
        trackedSession = Session("AdsAvailable")
    }

    override fun onSessionInitFailed() {
        trackedSession = Session("SessionFailed")
    }
}

class TestSessionAdapter: SessionAdapter {
    var initSent = false
    var adsRefreshed = false

    override suspend fun sendInit(deviceInfo: DeviceInfo, listener: SessionInitListener) {
        initSent = true
    }

    override suspend fun sendRefreshAds(session: Session, listener: AdGetListener) {
        adsRefreshed = true
    }

    fun reset() {
        initSent = false
        adsRefreshed = false
    }
}