package com.adadapted.android.sdk.core.device

import com.adadapted.android.sdk.core.concurrency.TransporterCoroutineScope
import com.adadapted.android.sdk.core.interfaces.DeviceCallback
import com.adadapted.android.sdk.tools.TestDeviceInfoExtractor
import com.adadapted.android.sdk.tools.TestTransporter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.Assert
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DeviceInfoClientTest {
    var testTransporter = UnconfinedTestDispatcher()
    private val testTransporterScope: TransporterCoroutineScope = TestTransporter(testTransporter)

    @Before
    fun setup() {
        Dispatchers.setMain(testTransporter)
        DeviceInfoClient.createInstance("", false, HashMap(), "", TestDeviceInfoExtractor(), testTransporterScope)

        DeviceInfoClient.getDeviceInfo(object: DeviceCallback{
            override fun onDeviceInfoCollected(deviceInfo: DeviceInfo) {
                //dummy
            }
        })
    }

    @Test
    fun testGetDeviceInfo() {
        DeviceInfoClient.createInstance("", false, HashMap(), "", TestDeviceInfoExtractor(), testTransporterScope)
        var deviceInfoResult = DeviceInfo()
        Assert.assertTrue(deviceInfoResult.deviceName.isEmpty())

        DeviceInfoClient.getDeviceInfo(object: DeviceCallback {
            override fun onDeviceInfoCollected(deviceInfo: DeviceInfo) {
                deviceInfoResult = deviceInfo
            }
        })

        Assert.assertEquals("TestDevice", deviceInfoResult.deviceName)
    }

    @Test
    fun testGetDeviceInfoWithCustomIdentifier() {
        DeviceInfoClient.createInstance("", false, HashMap(), "customUDID", TestDeviceInfoExtractor(), testTransporterScope)
        var deviceInfoResult = DeviceInfo()
        assert(deviceInfoResult.deviceName.isEmpty())

        DeviceInfoClient.getDeviceInfo(object: DeviceCallback {
            override fun onDeviceInfoCollected(deviceInfo: DeviceInfo) {
                deviceInfoResult = deviceInfo
            }
        })

        Assert.assertEquals("TestDevice", deviceInfoResult.deviceName)
        Assert.assertEquals("customUDID", deviceInfoResult.udid)
    }
}