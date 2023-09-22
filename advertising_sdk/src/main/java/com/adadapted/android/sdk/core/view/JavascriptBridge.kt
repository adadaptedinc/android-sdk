package com.adadapted.android.sdk.core.view

import android.webkit.JavascriptInterface
import com.adadapted.android.sdk.constants.EventStrings
import com.adadapted.android.sdk.core.ad.Ad
import com.adadapted.android.sdk.core.ad.AdContent.Companion.createAddToListContent
import com.adadapted.android.sdk.core.atl.AddItContentPublisher
import com.adadapted.android.sdk.core.atl.AddToListItem
import com.adadapted.android.sdk.core.atl.PopupContent.Companion.createPopupContent
import com.adadapted.android.sdk.core.event.EventClient

class JavascriptBridge internal constructor(private val ad: Ad) {
    @JavascriptInterface
    fun deliverAdContent() {
        val params = HashMap<String, String>()
        params["ad_id"] = ad.id
        EventClient.trackSdkEvent(EventStrings.POPUP_CONTENT_CLICKED, params)
        val content = createAddToListContent(ad)
        AddItContentPublisher.publishAdContent(content)
    }

    @JavascriptInterface
    fun addItemToList(
        payloadId: String,
        trackingId: String,
        title: String,
        brand: String,
        category: String,
        barCode: String,
        retailerSku: String,
        discount: String,
        productImage: String
    ) {
        val params = HashMap<String, String>()
        params["tracking_id"] = trackingId
        EventClient.trackSdkEvent(EventStrings.POPUP_ATL_CLICKED, params)

        val items: MutableList<AddToListItem> = ArrayList()
        items.add(
            AddToListItem(
                trackingId,
                title,
                brand,
                category,
                barCode,
                retailerSku,
                discount,
                productImage
            )
        )
        val content = createPopupContent(payloadId, items)
        AddItContentPublisher.publishPopupContent(content)
    }
}