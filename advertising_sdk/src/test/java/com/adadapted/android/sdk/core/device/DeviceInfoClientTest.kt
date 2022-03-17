package com.adadapted.android.sdk.core.device

import com.adadapted.android.sdk.core.concurrency.TransporterCoroutineScope
import com.adadapted.android.sdk.tools.TestDeviceInfoExtractor
import com.adadapted.android.sdk.tools.TestTransporter
import com.nhaarman.mockitokotlin2.mock
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test

class DeviceInfoClientTest {
    var testTransporter = TestCoroutineDispatcher()
    private val testTransporterScope: TransporterCoroutineScope = TestTransporter(testTransporter)

    @Before
    fun setup() {
        Dispatchers.setMain(testTransporter)
        DeviceInfoClient.createInstance(mock(),"", false, HashMap(), "", DeviceInfoExtractor(), testTransporterScope)

        DeviceInfoClient.getInstance().getDeviceInfo(object: DeviceInfoClient.Callback {
            override fun onDeviceInfoCollected(deviceInfo: DeviceInfo) {
                //dummy
            }
        })

    }

    @Test
    fun testGetDeviceInfo() {
        DeviceInfoClient.createInstance(mock(),"", false, HashMap(), "", TestDeviceInfoExtractor(), testTransporterScope)
        var deviceInfoResult = DeviceInfo()
        assertNull(deviceInfoResult.device)

        DeviceInfoClient.getInstance().getDeviceInfo(object: DeviceInfoClient.Callback {
            override fun onDeviceInfoCollected(deviceInfo: DeviceInfo) {
                deviceInfoResult = deviceInfo
            }
        })

        assertEquals("TestDevice", deviceInfoResult.device)
    }

    @Test
    fun testGetDeviceInfoWithCustomIdentifier() {
        DeviceInfoClient.createInstance(mock(),"", false, HashMap(), "customUDID", TestDeviceInfoExtractor(), testTransporterScope)
        var deviceInfoResult = DeviceInfo()
        assertNull(deviceInfoResult.device)

        DeviceInfoClient.getInstance().getDeviceInfo(object: DeviceInfoClient.Callback {
            override fun onDeviceInfoCollected(deviceInfo: DeviceInfo) {
                deviceInfoResult = deviceInfo
            }
        })

        assertEquals("TestDevice", deviceInfoResult.device)
        assertEquals("customUDID", deviceInfoResult.udid)
    }

    @Test
    fun deviceInfoProdIsChangedCorrectly() {
        val deviceInfoResult = DeviceInfo()
        assertEquals(deviceInfoResult.isProd, false)
        deviceInfoResult.isProd = true
        assertEquals(deviceInfoResult.isProd, true)
    }
}