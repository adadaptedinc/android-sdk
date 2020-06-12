package com.adadapted.android.sdk.core.device

import androidx.test.platform.app.InstrumentationRegistry
import com.adadapted.android.sdk.core.concurrency.TransporterCoroutineScope
import com.adadapted.android.sdk.tools.TestAdvertisingIdClientWrapper
import com.adadapted.android.sdk.tools.TestTransporter
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DeviceInfoClientTest {
    var testTransporter = TestCoroutineDispatcher()
    val testTransporterScope: TransporterCoroutineScope = TestTransporter(testTransporter)
    val testAdvertisingIdClientWrapper = TestAdvertisingIdClientWrapper()

    @Before
    fun setup() {
        Dispatchers.setMain(testTransporter)
        DeviceInfoClient.createInstance(InstrumentationRegistry.getInstrumentation().targetContext,"", false, HashMap(), testAdvertisingIdClientWrapper, testTransporterScope)
    }

    @Test
    fun testGetDeviceInfo() {
        var deviceInfoResult = DeviceInfo()
        assertNull(deviceInfoResult.device)

        DeviceInfoClient.getInstance().getDeviceInfo(object: DeviceInfoClient.Callback {
            override fun onDeviceInfoCollected(deviceInfo: DeviceInfo) {
                deviceInfoResult = deviceInfo
            }
        })

        assertEquals("unknown robolectric", deviceInfoResult.device)
    }
}