package com.adadapted.android.sdk.ui.activity

import android.webkit.JavascriptInterface
import com.adadapted.android.sdk.config.EventStrings
import com.adadapted.android.sdk.core.ad.Ad
import com.adadapted.android.sdk.core.ad.AdContent.CREATOR.createAddToListContent
import com.adadapted.android.sdk.core.atl.AddToListItem
import com.adadapted.android.sdk.core.atl.PopupContent.CREATOR.createPopupContent
import com.adadapted.android.sdk.core.event.AppEventClient.Companion.getInstance
import com.adadapted.android.sdk.ui.messaging.AdditContentPublisher

class PopupJavascriptBridge internal constructor(private val ad: Ad) {
    @JavascriptInterface
    fun deliverAdContent() {
        val params = HashMap<String, String>()
        params["ad_id"] = ad.id
        getInstance().trackSdkEvent(EventStrings.POPUP_CONTENT_CLICKED, params)
        val content = createAddToListContent(ad)
        AdditContentPublisher.getInstance().publishAdContent(content)
    }

    @JavascriptInterface
    fun addItemToList(payloadId: String,
                      trackingId: String,
                      title: String,
                      brand: String,
                      category: String,
                      barCode: String,
                      retailerSku: String,
                      discount: String,
                      productImage: String) {

        val params = HashMap<String, String>()
        params["tracking_id"] = trackingId
        getInstance().trackSdkEvent(EventStrings.POPUP_ATL_CLICKED, params)

        val items: MutableList<AddToListItem> = ArrayList()
        items.add(AddToListItem(
                trackingId,
                title,
                brand,
                category,
                barCode,
                retailerSku,
                discount,
                productImage
        ))
        val content = createPopupContent(payloadId, items)
        AdditContentPublisher.getInstance().publishPopupContent(content)
    }

    companion object {
        private val TAG = PopupJavascriptBridge::class.java.name
    }
}