package com.adadapted.android.sdk.core.device

import android.content.Context
import com.adadapted.android.sdk.core.concurrency.TransporterCoroutineScope
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock

class DeviceInfoClient private constructor(
        private val context: Context,
        private val appId: String,
        private val isProd: Boolean,
        private val params: Map<String, String>,
        private val deviceInfoExtractor: InfoExtractor,
        private val transporter: TransporterCoroutineScope) {

    interface Callback {
        fun onDeviceInfoCollected(deviceInfo: DeviceInfo)
    }

    private lateinit var deviceInfo: DeviceInfo
    private val lock: Lock = ReentrantLock()
    private val callbacks: MutableSet<Callback>

    private fun performGetInfo(callback: Callback) {
        lock.lock()
        try {
            if (this::deviceInfo.isInitialized) {
                callback.onDeviceInfoCollected(deviceInfo)
            } else {
                callbacks.add(callback)
            }
        } finally {
            lock.unlock()
        }
    }

   private fun collectDeviceInfo() {
       lock.lock()
       try {
           this.deviceInfo = deviceInfoExtractor.extractDeviceInfo(context, appId, isProd, params)
       } finally {
           lock.unlock()
       }
       notifyCallbacks()
   }

    private fun notifyCallbacks() {
        lock.lock()
        try {
            val currentCallbacks: Set<Callback> = HashSet(callbacks)
            for (caller in currentCallbacks) {
                caller.onDeviceInfoCollected(deviceInfo)
                callbacks.remove(caller)
            }
        } finally {
            lock.unlock()
        }
    }


    fun getDeviceInfo(callback: Callback) {
        transporter.dispatchToBackground {
            performGetInfo(callback)
        }
    }

    companion object {
        private lateinit var instance: DeviceInfoClient

        fun createInstance(context: Context,
                           appId: String,
                           isProd: Boolean,
                           params: Map<String, String>,
                           deviceInfoExtractor: InfoExtractor = DeviceInfoExtractor(),
                           transporter: TransporterCoroutineScope) {
            instance = DeviceInfoClient(context, appId, isProd, params, deviceInfoExtractor, transporter)
        }

        fun getInstance(): DeviceInfoClient {
            return instance
        }
    }

    init {
        callbacks = HashSet()
        transporter.dispatchToBackground {
            collectDeviceInfo()
        }
    }
}