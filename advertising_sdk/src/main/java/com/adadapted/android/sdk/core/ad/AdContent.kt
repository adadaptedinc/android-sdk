package com.adadapted.android.sdk.core.ad

import com.adadapted.android.sdk.config.EventStrings
import com.adadapted.android.sdk.core.atl.AddToListContent
import com.adadapted.android.sdk.core.atl.AddToListContent.Sources
import com.adadapted.android.sdk.core.atl.AddToListItem
import com.adadapted.android.sdk.core.event.AppEventClient
import java.util.Locale
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock
import kotlin.collections.HashMap

class AdContent private constructor(private val ad: Ad, val type: Int, private val items: List<AddToListItem>, adClient: AdEventClient = AdEventClient.getInstance(), appClient: AppEventClient = AppEventClient.getInstance()) : AddToListContent {
    private var isHandled: Boolean
    private val lock: Lock = ReentrantLock()
    private var adEventClient: AdEventClient = adClient
    private var appEventClient: AppEventClient = appClient

    @Synchronized
    override fun acknowledge() {
        lock.lock()
        try {
            if (isHandled) {
                return
            }
            isHandled = true
            adEventClient.trackInteraction(ad)
        } finally {
            lock.unlock()
        }
    }

    @Synchronized
    override fun itemAcknowledge(item: AddToListItem) {
        lock.lock()
        try {
            if (!isHandled) {
                isHandled = true
                adEventClient.trackInteraction(ad)
            }
            trackItem(item.title)
        } finally {
            lock.unlock()
        }
    }

    @Synchronized
    private fun trackItem(itemName: String) {
        val params: MutableMap<String, String> = HashMap()
        params[AD_ID] = ad.id
        params[ITEM_NAME] = itemName
        appEventClient.trackSdkEvent(EventStrings.ATL_ITEM_ADDED_TO_LIST, params)
    }

    @Synchronized
    override fun failed(message: String) {
        lock.lock()
        try {
            if (isHandled) {
                return
            }
            isHandled = true
            val params: MutableMap<String, String> = HashMap()
            params[AD_ID] = ad.id
            appEventClient.trackError(EventStrings.ATL_ADDED_TO_LIST_FAILED,
                    if (message.isEmpty()) UNKNOWN_REASON else message,
                    params)
        } finally {
            lock.unlock()
        }
    }

    override fun itemFailed(item: AddToListItem, message: String) {
        lock.lock()
        try {
            isHandled = true
            val params: MutableMap<String, String> = HashMap()
            params[AD_ID] = ad.id
            params[ITEM] = item.title
            appEventClient.trackError(EventStrings.ATL_ADDED_TO_LIST_ITEM_FAILED,
                    if (message.isEmpty()) UNKNOWN_REASON else message,
                    params)
        } finally {
            lock.unlock()
        }
    }

    val zoneId: String
        get() = ad.zoneId

    override fun getItems(): List<AddToListItem> {
        return items
    }

    override fun hasNoItems(): Boolean {
        return items.isEmpty()
    }

    override fun getSource(): String {
        return Sources.IN_APP
    }

    companion object {
        private const val ADD_TO_LIST = 0
        private const val AD_ID = "ad_id"
        private const val ITEM_NAME = "item_name"
        private const val ITEM = "item"
        private const val UNKNOWN_REASON = "Unknown Reason"

        fun createAddToListContent(ad: Ad): AdContent {
            return AdContent(ad, ADD_TO_LIST, ad.payload.detailedListItems)
        }
    }

    init {
        if (ad.payload.detailedListItems.isEmpty()) {
            appClient.trackError(EventStrings.AD_PAYLOAD_IS_EMPTY, String.format(Locale.ENGLISH, "Ad %s has empty payload", ad.id))
        }
        isHandled = false
    }
}