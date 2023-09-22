package com.adadapted.android.sdk.core.ad

import com.adadapted.android.sdk.config.EventStrings
import com.adadapted.android.sdk.core.atl.AddToListContent
import com.adadapted.android.sdk.core.atl.AddToListItem
import kotlin.collections.HashMap

class AdContent private constructor(
    private val ad: Ad,
    private val items: List<AddToListItem>,
    private val eventClient: EventClient = EventClient
) : AddToListContent {
    private var isHandled: Boolean

    override fun acknowledge() {
        if (isHandled) {
            return
        }
        isHandled = true
        eventClient.trackInteraction(ad)
    }

    @Synchronized
    override fun itemAcknowledge(item: AddToListItem) {
        if (!isHandled) {
            isHandled = true
            eventClient.trackInteraction(ad)
        }
        trackItem(item.title)
    }

    @Synchronized
    private fun trackItem(itemName: String) {
        val params: MutableMap<String, String> = HashMap()
        params[AD_ID] = ad.id
        params[ITEM_NAME] = itemName
        eventClient.trackSdkEvent(EventStrings.ATL_ITEM_ADDED_TO_LIST, params)
    }

    @Synchronized
    override fun failed(message: String) {
        if (isHandled) {
            return
        }
        isHandled = true
        val params: MutableMap<String, String> = HashMap()
        params[AD_ID] = ad.id
        eventClient.trackSdkError(
            EventStrings.ATL_ADDED_TO_LIST_FAILED,
            message.ifEmpty { UNKNOWN_REASON },
            params
        )
    }

    override fun itemFailed(item: AddToListItem, message: String) {
        isHandled = true
        val params: MutableMap<String, String> = HashMap()
        params[AD_ID] = ad.id
        params[ITEM] = item.title
        eventClient.trackSdkError(
            EventStrings.ATL_ADDED_TO_LIST_ITEM_FAILED,
            message.ifEmpty { UNKNOWN_REASON },
            params
        )
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
        return AddToListContent.Sources.IN_APP
    }

    companion object {
        private const val AD_ID = "ad_id"
        private const val ITEM_NAME = "item_name"
        private const val ITEM = "item"
        private const val UNKNOWN_REASON = "Unknown Reason"

        fun createAddToListContent(ad: Ad): AdContent {
            return AdContent(ad, ad.payload.detailedListItems)
        }
    }

    init {
        if (ad.payload.detailedListItems.isEmpty()) {
            eventClient.trackSdkError(
                EventStrings.AD_PAYLOAD_IS_EMPTY,
                "Ad ${ad.id} has empty payload"
            )
        }
        isHandled = false
    }
}