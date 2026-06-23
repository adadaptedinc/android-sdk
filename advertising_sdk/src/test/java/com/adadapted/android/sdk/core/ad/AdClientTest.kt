package com.adadapted.android.sdk.core.ad

import com.adadapted.android.sdk.core.concurrency.TransporterCoroutineScope
import com.adadapted.android.sdk.core.interfaces.AdAdapter
import com.adadapted.android.sdk.core.interfaces.ZoneAdListener
import com.adadapted.android.sdk.tools.TestTransporter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AdClientTest {
    private var testTransporter = UnconfinedTestDispatcher()
    private val testTransporterScope: TransporterCoroutineScope = TestTransporter(testTransporter)
    private var testAdapter = TestAdClientAdapter()

    @Before
    fun setup() {
        Dispatchers.setMain(testTransporter)
        AdClient.createInstance(testAdapter, testTransporterScope)
    }

    @Test
    fun `fetchNewAd delegates to adapter when initialized`() {
        val listener = TestZoneAdListener()

        AdClient.fetchNewAd("testZone", listener, storeId = "store1", contextId = "ctx1")

        assertEquals("testZone", testAdapter.lastRequestedZoneId)
        assertEquals("store1", testAdapter.lastStoreId)
        assertEquals("ctx1", testAdapter.lastContextId)
        assertNotNull(listener.loadedData)
    }

    @Test
    fun `fetchNewAd passes extra parameter`() {
        val listener = TestZoneAdListener()

        AdClient.fetchNewAd("zoneX", listener, extra = "extraData")

        assertEquals("extraData", testAdapter.lastExtra)
    }

    @Test
    fun `fetchNewAd calls listener onAdLoaded`() {
        val listener = TestZoneAdListener()

        AdClient.fetchNewAd("zone1", listener)

        assertNotNull(listener.loadedData)
        assertEquals(false, listener.loadFailed)
    }

    @Test
    fun `fetchNewAd with default parameters`() {
        val listener = TestZoneAdListener()

        AdClient.fetchNewAd("zoneDefault", listener)

        assertEquals("zoneDefault", testAdapter.lastRequestedZoneId)
        assertEquals("", testAdapter.lastStoreId)
        assertEquals("", testAdapter.lastContextId)
        assertEquals("", testAdapter.lastExtra)
    }

    @Test
    fun `createInstance allows swapping adapters`() {
        val newAdapter = TestAdClientAdapter()
        AdClient.createInstance(newAdapter, testTransporterScope)

        val listener = TestZoneAdListener()
        AdClient.fetchNewAd("newZone", listener)

        assertEquals("newZone", newAdapter.lastRequestedZoneId)
    }
}

private class TestAdClientAdapter : AdAdapter {
    var lastRequestedZoneId: String = ""
    var lastStoreId: String = ""
    var lastContextId: String = ""
    var lastExtra: String = ""

    override suspend fun requestAd(
        zoneId: String,
        listener: ZoneAdListener,
        storeId: String,
        contextId: String,
        extra: String
    ) {
        lastRequestedZoneId = zoneId
        lastStoreId = storeId
        lastContextId = contextId
        lastExtra = extra
        listener.onAdLoaded(AdZoneData())
    }
}

private class TestZoneAdListener : ZoneAdListener {
    var loadedData: AdZoneData? = null
    var loadFailed = false

    override fun onAdLoaded(adZoneData: AdZoneData) {
        loadedData = adZoneData
    }

    override fun onAdLoadFailed() {
        loadFailed = true
    }
}
