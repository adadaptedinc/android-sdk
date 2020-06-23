package com.adadapted.android.sdk.ui.activity;

import android.webkit.JavascriptInterface;

import com.adadapted.android.sdk.config.EventStrings;
import com.adadapted.android.sdk.core.ad.Ad;
import com.adadapted.android.sdk.core.atl.AddToListItem;
import com.adadapted.android.sdk.core.atl.PopupContent;
import com.adadapted.android.sdk.core.event.AppEventClient;
import com.adadapted.android.sdk.ui.messaging.AdditContentPublisher;
import com.adadapted.android.sdk.core.ad.AdContent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PopupJavascriptBridge {
    private static final String TAG = PopupJavascriptBridge.class.getName();

    private final Ad ad;

    PopupJavascriptBridge(final Ad ad) {
        this.ad = ad;
    }

    @JavascriptInterface
    public void deliverAdContent() {
        final HashMap<String, String> params = new HashMap<>();
        params.put("ad_id", ad.getId());
        AppEventClient.Companion.getInstance().trackSdkEvent(EventStrings.POPUP_CONTENT_CLICKED, params);

        final AdContent content = AdContent.CREATOR.createAddToListContent(ad);
        AdditContentPublisher.getInstance().publishAdContent(content);
    }

    @JavascriptInterface
    public void addItemToList(final String payloadId,
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
        AppEventClient.Companion.getInstance().trackSdkEvent(EventStrings.POPUP_ATL_CLICKED, params);

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

        final PopupContent content = PopupContent.CREATOR.createPopupContent(
                payloadId,
                items
        );

        AdditContentPublisher.getInstance().publishPopupContent(content);
    }
}
