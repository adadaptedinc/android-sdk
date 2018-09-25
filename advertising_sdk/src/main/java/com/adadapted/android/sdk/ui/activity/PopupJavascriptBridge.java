package com.adadapted.android.sdk.ui.activity;

import android.webkit.JavascriptInterface;

import com.adadapted.android.sdk.core.ad.Ad;
import com.adadapted.android.sdk.core.atl.AddToListItem;
import com.adadapted.android.sdk.core.atl.PopupContent;
import com.adadapted.android.sdk.core.event.AppEventClient;
import com.adadapted.android.sdk.ui.messaging.AdditContentPublisher;
import com.adadapted.android.sdk.core.ad.AdContent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

class PopupJavascriptBridge {
    private static final String TAG = PopupJavascriptBridge.class.getName();

    private final Ad ad;

    PopupJavascriptBridge(final Ad ad) {
        this.ad = ad;
    }

    @JavascriptInterface
    void deliverAdContent() {
        final HashMap<String, String> params = new HashMap<>();
        params.put("ad_id", ad.getId());
        AppEventClient.trackSdkEvent("popup_content_clicked", params);

        final AdContent content = AdContent.createAddToListContent(ad);
        AdditContentPublisher.getInstance().publishAdContent(content);
    }

    @JavascriptInterface
    void addItemToList(final String payloadId,
                       final String trackingId,
                       final String title,
                       final String brand,
                       final String category,
                       final String barCode,
                       final String retailerSku,
                       final String discount,
                       final String productImage) {
        final HashMap<String, String> params = new HashMap<>();
        params.put("tracking_id", trackingId);
        AppEventClient.trackSdkEvent("popup_atl_clicked", params);

        final List<AddToListItem> items = new ArrayList<>();
        items.add(new AddToListItem(
                trackingId,
                title,
                brand,
                category,
                barCode,
                retailerSku,
                discount,
                productImage
        ));

        final PopupContent content = PopupContent.createPopupContent(
                payloadId,
                items
        );

        AdditContentPublisher.getInstance().publishPopupContent(content);
    }
}
