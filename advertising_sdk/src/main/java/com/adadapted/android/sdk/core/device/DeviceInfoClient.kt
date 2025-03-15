package com.adadapted.android.sdk.core.device

import com.adadapted.android.sdk.core.concurrency.Transporter
import com.adadapted.android.sdk.core.concurrency.TransporterCoroutineScope
import com.adadapted.android.sdk.core.interfaces.DeviceCallback

object DeviceInfoClient {

    private var appId: String = ""
    private var isProd: Boolean = false
    private var params: Map<String, String> = emptyMap()
    private var customIdentifier: String =""
    private lateinit var deviceInfoExtractor: DeviceInfoExtractor
    private var transporter: TransporterCoroutineScope = Transporter()
    private var deviceInfo: DeviceInfo = DeviceInfo()
    private var deviceCallbacks: MutableSet<DeviceCallback> = HashSet()

    private fun performGetInfo(deviceCallback: DeviceCallback) {
        deviceInfo.let { deviceCallback.onDeviceInfoCollected(it) }
    }

    private fun collectDeviceInfo() {
        deviceInfo = deviceInfoExtractor.extractDeviceInfo(appId, isProd, customIdentifier, params)
        notifyCallbacks()
    }

    private fun notifyCallbacks() { //TODO is this necessary anymore?
        val currentDeviceCallbacks: Set<DeviceCallback> = HashSet(deviceCallbacks)
        for (caller in currentDeviceCallbacks) {
            deviceInfo.let { caller.onDeviceInfoCollected(it) }
            deviceCallbacks.remove(caller)
        }
    }

    fun getDeviceInfo(deviceCallback: DeviceCallback) {
        transporter.dispatchToThread {
            performGetInfo(deviceCallback)
        }
    }

    fun getCachedDeviceInfo(): DeviceInfo {
        return deviceInfo
    }

    fun createInstance(
        appId: String,
        isProd: Boolean,
        params: Map<String, String>,
        customIdentifier: String,
        deviceInfoExtractor: DeviceInfoExtractor,
        transporter: TransporterCoroutineScope
    ) {
            DeviceInfoClient.appId = appId
            DeviceInfoClient.isProd = isProd
            DeviceInfoClient.params = params
            DeviceInfoClient.customIdentifier = customIdentifier
            DeviceInfoClient.deviceInfoExtractor = deviceInfoExtractor
            DeviceInfoClient.transporter = transporter

        transporter.dispatchToThread {
            collectDeviceInfo()
        }
    }
}