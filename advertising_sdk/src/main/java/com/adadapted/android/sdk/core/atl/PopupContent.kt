package com.adadapted.android.sdk.core.atl

import com.adadapted.android.sdk.constants.EventStrings
import com.adadapted.android.sdk.core.event.EventClient

class PopupContent(val payloadId: String, private val items: List<AddToListItem>) :
    AddToListContent {
    private var handled = false

    override fun acknowledge() {
        if (!handled) {
            handled = true
            markPopupContentAcknowledged(this)
        }
    }

    override fun itemAcknowledge(item: AddToListItem) {
        if (!handled) {
            handled = true
            markPopupContentAcknowledged(this)
        }
        markPopupContentItemAcknowledged(this, item)
    }

    override fun failed(message: String) {
        if (!handled) {
            handled = true
            markPopupContentFailed(this, message)
        }
    }

    override fun itemFailed(item: AddToListItem, message: String) {
        markPopupContentItemFailed(this, item, message)
    }

    override fun getSource(): String {
        return AddToListContent.Sources.IN_APP
    }

    override fun getItems(): List<AddToListItem> {
        return items
    }

    override fun hasNoItems(): Boolean {
        return items.isEmpty()
    }

    companion object {
        fun createPopupContent(payloadId: String, items: List<AddToListItem>): PopupContent {
            return PopupContent(payloadId, items)
        }

        fun markPopupContentAcknowledged(content: PopupContent) {
            val params: MutableMap<String, String> = HashMap()
            params[PAYLOAD_ID] = content.payloadId
            EventClient.trackSdkEvent(EventStrings.POPUP_ADDED_TO_LIST, params)
        }

        fun markPopupContentItemAcknowledged(content: PopupContent, item: AddToListItem) {
            val params: MutableMap<String, String> = HashMap()
            params[PAYLOAD_ID] = content.payloadId
            params[TRACKING_ID] = item.trackingId
            params[ITEM_NAME] = item.title
            EventClient.trackSdkEvent(EventStrings.POPUP_ITEM_ADDED_TO_LIST, params)
        }

        fun markPopupContentFailed(content: PopupContent, message: String) {
            val eventParams: MutableMap<String, String> = HashMap()
            eventParams[PAYLOAD_ID] = content.payloadId
            EventClient.trackSdkError(EventStrings.POPUP_CONTENT_FAILED, message, eventParams)
        }

        fun markPopupContentItemFailed(
            content: PopupContent,
            item: AddToListItem,
            message: String
        ) {
            val eventParams: MutableMap<String, String> = HashMap()
            eventParams[PAYLOAD_ID] = content.payloadId
            eventParams[TRACKING_ID] = item.trackingId
            EventClient.trackSdkError(EventStrings.POPUP_CONTENT_ITEM_FAILED, message, eventParams)
        }

        private const val PAYLOAD_ID = "payload_id"
        private const val TRACKING_ID = "tracking_id"
        private const val ITEM_NAME = "item_name"
    }
}