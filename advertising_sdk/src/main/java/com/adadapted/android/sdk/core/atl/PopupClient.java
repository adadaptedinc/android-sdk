package com.adadapted.android.sdk.core.atl;

import com.adadapted.android.sdk.core.concurrency.ThreadPoolInteractorExecuter;
import com.adadapted.android.sdk.core.event.AppEventClient;

import java.util.HashMap;
import java.util.Map;

class PopupClient {
    static void markPopupContentAcknowledged(final PopupContent content) {
        ThreadPoolInteractorExecuter.getInstance().executeInBackground(new Runnable() {
            @Override
            public void run() {
                final Map<String, String> params = new HashMap<>();
                params.put("payload_id", content.getPayloadId());

                AppEventClient.trackSdkEvent("popup_added_to_list", params);
            }
        });
    }

    static void markPopupContentItemAcknowledged(final PopupContent content, final AddToListItem item) {
        ThreadPoolInteractorExecuter.getInstance().executeInBackground(new Runnable() {
            @Override
            public void run() {
                final Map<String, String> params = new HashMap<>();
                params.put("payload_id", content.getPayloadId());
                params.put("tracking_id", item.getTrackingId());
                params.put("item_name", item.getTitle());

                AppEventClient.trackSdkEvent("popup_item_added_to_list", params);
            }
        });
    }

    static void markPopupContentFailed(final PopupContent content, final String message) {
        ThreadPoolInteractorExecuter.getInstance().executeInBackground(new Runnable() {
            @Override
            public void run() {
                final Map<String, String> eventParams = new HashMap<>();
                eventParams.put("payload_id", content.getPayloadId());

                AppEventClient.trackError("POPUP_CONTENT_FAILED", message, eventParams);
            }
        });
    }

    static void markPopupContentItemFailed(final PopupContent content, final AddToListItem item, final String message) {
        ThreadPoolInteractorExecuter.getInstance().executeInBackground(new Runnable() {
            @Override
            public void run() {
                final Map<String, String> eventParams = new HashMap<>();
                eventParams.put("payload_id", content.getPayloadId());
                eventParams.put("tracking_id", item.getTrackingId());

                AppEventClient.trackError("POPUP_CONTENT_ITEM_FAILED", message, eventParams);
            }
        });
    }
}
