package com.adadapted.android.sdk.core.ad

import com.adadapted.android.sdk.core.concurrency.TransporterCoroutineScope
import com.adadapted.android.sdk.core.session.SessionClient
import com.adadapted.android.sdk.ext.http.HttpAdEventSink
import com.adadapted.android.sdk.tools.TestTransporter
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import junit.framework.Assert.assertNotNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class AdEventClientTest {

    var mockAdEventSink = mock<HttpAdEventSink>()
    var testTransporter = TestCoroutineDispatcher()
    val testTransporterScope: TransporterCoroutineScope = TestTransporter(testTransporter)
    var mockImpressionIdCounter = mock<Counter>()
    var testAdEventClient = AdEventClient
    var testAd = Ad("testId")

    @Before
    fun setup() {
        Dispatchers.setMain(testTransporter)
        whenever(mockAdEventSink.sendBatch(any(),any())).then { }
        whenever(mockImpressionIdCounter.getCurrentCountFor(any())).thenReturn(1)

        testAdEventClient.createInstance(mockAdEventSink, testTransporterScope, mockImpressionIdCounter)
    }

    @Test
    fun createInstance() {
        assertNotNull(testAdEventClient)
    }

    // TODO needs session client refactor first for all of the other tests
    @Test
    fun addListener() {
//        val mockListener = mock<AdEventClient.Listener>()
//        testAdEventClient.getInstance().addListener(mock())
//        testAdEventClient.trackImpression(testAd)
//
//        verify(mockListener).onAdEventTracked(any())
    }

    @Test
    fun removeListener() {
        Assert.assertTrue(true)
    }

    @Test
    fun trackImpression() {
        Assert.assertTrue(true)
    }

    @Test
    fun trackImpressionEnd() {
        Assert.assertTrue(true)
    }

    @Test
    fun trackInteraction() {
        Assert.assertTrue(true)
    }

    @Test
    fun trackPopupBegin() {
        Assert.assertTrue(true)
    }

    @Test
    fun trackPopupEnd() {
        Assert.assertTrue(true)
    }

    @Test
    fun publishEvents() {
        Assert.assertTrue(true)
    }

    @Test
    fun onSessionAvailable() {
        Assert.assertTrue(true)
    }

    @Test
    fun onAdsAvailable() {
        Assert.assertTrue(true)
    }

    @Test
    fun onSessionInitFailed() {
        Assert.assertTrue(true)
    }

}