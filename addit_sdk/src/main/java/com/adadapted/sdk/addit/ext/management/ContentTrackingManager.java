package com.adadapted.sdk.addit.ext.management;

import com.adadapted.sdk.addit.core.app.AppEventSource;
import com.adadapted.sdk.addit.core.content.AddToListItem;
import com.adadapted.sdk.addit.core.content.Content;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chrisweeden on 3/3/17.
 */

public class ContentTrackingManager {
    public static void markContentAcknowledged(final Content content) {
        final List<AddToListItem> payload = content.getPayload();
        for (AddToListItem item : payload) {
            final Map<String, String> eventParams = new HashMap<>();
            eventParams.put("payload_id", content.getPayloadId());
            eventParams.put("tracking_id", item.getTrackingId());
            eventParams.put("item_name", item.getTitle());
            eventParams.put("source", content.getSource());

            AppEventTrackingManager.registerEvent(AppEventSource.SDK, "addit_added_to_list", eventParams);

            if(content.isPayloadSource()) {
                PayloadDropoffManager.trackDelivered(content.getPayloadId());
            }
        }
    }

    public static void markContentDuplicate(final Content content) {
        final Map<String, String> eventParams = new HashMap<>();
        eventParams.put("payload_id", content.getPayloadId());

        AppEventTrackingManager.registerEvent(AppEventSource.SDK, "addit_duplicate_payload", eventParams);

        if(content.isPayloadSource()) {
            PayloadDropoffManager.trackRejected(content.getPayloadId());
        }
    }

    public static void markContentFailed(final Content content, final String message) {
        final Map<String, String> eventParams = new HashMap<>();
        eventParams.put("payload_id", content.getPayloadId());

        AppErrorTrackingManager.registerEvent("ADDIT_CONTENT_FAILED", message, eventParams);

        if(content.isPayloadSource()) {
            PayloadDropoffManager.trackRejected(content.getPayloadId());
        }
    }
}
