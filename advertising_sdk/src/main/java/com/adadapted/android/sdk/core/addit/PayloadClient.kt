package com.adadapted.android.sdk.core.addit

import com.adadapted.android.sdk.config.EventStrings
import com.adadapted.android.sdk.core.atl.AddToListItem
import com.adadapted.android.sdk.core.concurrency.TransporterCoroutineScope
import com.adadapted.android.sdk.core.device.DeviceInfo
import com.adadapted.android.sdk.core.device.DeviceInfoClient
import com.adadapted.android.sdk.core.event.AppEventClient
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock

class PayloadClient private constructor(private val adapter: PayloadAdapter, private val appEventClient: AppEventClient, private val transporter: TransporterCoroutineScope) {
    interface Callback {
        fun onPayloadAvailable(content: List<AdditContent>)
    }

    private fun performPickupPayload(callback: Callback) {
        DeviceInfoClient.getInstance().getDeviceInfo(object : DeviceInfoClient.Callback {
            override fun onDeviceInfoCollected(deviceInfo: DeviceInfo) {
                appEventClient.trackSdkEvent(EventStrings.PAYLOAD_PICKUP_ATTEMPT)

                adapter.pickup(deviceInfo, object: PayloadAdapter.Callback {
                    override fun onSuccess(content: List<AdditContent>) {
                        callback.onPayloadAvailable(content)
                    }
                })
            }
        })
    }

    private fun trackPayload(content: AdditContent, result: String) {
        val event = PayloadEvent(content.payloadId, result)
        adapter.publishEvent(event)
    }

    @Synchronized
    fun pickupPayloads(callback: Callback) {
        if (deeplinkInProgress) {
            return
        }
        transporter.dispatchToBackground {
            performPickupPayload(callback)
        }
    }

    @Synchronized
    fun deeplinkInProgress() {
        lock.lock()
        deeplinkInProgress = try {
            true
        } finally {
            lock.unlock()
        }
    }

    @Synchronized
    fun deeplinkCompleted() {
        lock.lock()
        deeplinkInProgress = try {
            false
        } finally {
            lock.unlock()
        }
    }

    @Synchronized
    fun markContentAcknowledged(content: AdditContent) {
        transporter.dispatchToBackground {
            val eventParams: MutableMap<String, String> = HashMap()
            eventParams[PAYLOAD_ID] = content.payloadId
            eventParams[SOURCE] = content.additSource
            appEventClient.trackSdkEvent(EventStrings.ADDIT_ADDED_TO_LIST, eventParams)
            if (content.isPayloadSource) {
                trackPayload(content, "delivered")
            }
        }
    }

    @Synchronized
    fun markContentItemAcknowledged(content: AdditContent, item: AddToListItem) {
        transporter.dispatchToBackground {
            val eventParams: MutableMap<String, String> = HashMap()
            eventParams[PAYLOAD_ID] = content.payloadId
            eventParams[TRACKING_ID] = item.trackingId
            eventParams[ITEM_NAME] = item.title
            eventParams[SOURCE] = content.additSource
            appEventClient.trackSdkEvent(EventStrings.ADDIT_ITEM_ADDED_TO_LIST, eventParams)
        }
    }

    @Synchronized
    fun markContentDuplicate(content: AdditContent) {
        transporter.dispatchToBackground {
            val eventParams: MutableMap<String, String> = HashMap()
            eventParams[PAYLOAD_ID] = content.payloadId
            appEventClient.trackSdkEvent(EventStrings.ADDIT_DUPLICATE_PAYLOAD, eventParams)
            if (content.isPayloadSource) {
                trackPayload(content, "duplicate")
            }
        }
    }

    @Synchronized
    fun markContentFailed(content: AdditContent, message: String) {
        transporter.dispatchToBackground {
            val eventParams: MutableMap<String, String> = HashMap()
            eventParams[PAYLOAD_ID] = content.payloadId
            appEventClient.trackError(EventStrings.ADDIT_CONTENT_FAILED, message, eventParams)
            if (content.isPayloadSource) {
                trackPayload(content, "rejected")
            }
        }
    }

    @Synchronized
    fun markContentItemFailed(content: AdditContent, item: AddToListItem, message: String) {
        transporter.dispatchToBackground {
            val eventParams: MutableMap<String, String> = HashMap()
            eventParams[PAYLOAD_ID] = content.payloadId
            eventParams[TRACKING_ID] = item.trackingId
            appEventClient.trackError(EventStrings.ADDIT_CONTENT_ITEM_FAILED, message, eventParams)
        }
    }

    companion object {
        private val LOGTAG = PayloadClient::class.java.name
        private const val PAYLOAD_ID = "payload_id"
        private const val TRACKING_ID = "tracking_id"
        private const val SOURCE = "source"
        private const val ITEM_NAME = "item_name"
        private lateinit var instance: PayloadClient
        private var deeplinkInProgress = false
        private val lock: Lock = ReentrantLock()

        fun getInstance(): PayloadClient {
            return instance
        }

        fun createInstance(adapter: PayloadAdapter, appEventClient: AppEventClient, transporter: TransporterCoroutineScope) {
            instance = PayloadClient(adapter, appEventClient, transporter)
        }
    }

}