package com.adadapted.android.sdk.core.atl

import com.adadapted.android.sdk.config.EventStrings
import com.adadapted.android.sdk.core.event.AppEventClient.Companion.getInstance

internal object PopupClient {
    fun markPopupContentAcknowledged(content: PopupContent) {
        val params: MutableMap<String, String> = HashMap()
        params["payload_id"] = content.payloadId
        getInstance().trackSdkEvent(EventStrings.POPUP_ADDED_TO_LIST, params)
    }

    fun markPopupContentItemAcknowledged(content: PopupContent, item: AddToListItem) {
        val params: MutableMap<String, String> = HashMap()
        params["payload_id"] = content.payloadId
        params["tracking_id"] = item.trackingId
        params["item_name"] = item.title
        getInstance().trackSdkEvent(EventStrings.POPUP_ITEM_ADDED_TO_LIST, params)
    }

    fun markPopupContentFailed(content: PopupContent, message: String) {
        val eventParams: MutableMap<String, String> = HashMap()
        eventParams["payload_id"] = content.payloadId
        getInstance().trackError(EventStrings.POPUP_CONTENT_FAILED, message, eventParams)
    }

    fun markPopupContentItemFailed(content: PopupContent, item: AddToListItem, message: String) {
        val eventParams: MutableMap<String, String> = HashMap()
        eventParams["payload_id"] = content.payloadId
        eventParams["tracking_id"] = item.trackingId
        getInstance().trackError(EventStrings.POPUP_CONTENT_ITEM_FAILED, message, eventParams)
    }
}