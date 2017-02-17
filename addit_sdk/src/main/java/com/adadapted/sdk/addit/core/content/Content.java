package com.adadapted.sdk.addit.core.content;

import com.adadapted.sdk.addit.core.app.AppEventSource;
import com.adadapted.sdk.addit.core.deeplink.DeeplinkContent;
import com.adadapted.sdk.addit.ext.management.AppErrorTrackingManager;
import com.adadapted.sdk.addit.ext.management.AppEventTrackingManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chrisweeden on 9/26/16.
 */
public abstract class Content {
    private final String payloadId;
    private final String message;
    private final String image;
    private final int type;
    private final List<AddToListItem> payload;

    public Content(final String payloadId,
                   final String message,
                   final String image,
                   final int type,
                   final List<AddToListItem> payload) {
        this.payloadId = payloadId;
        this.message = message;
        this.image = image;
        this.type = type;
        this.payload = payload;
    }

    public void acknowledge() {
        final List<AddToListItem> payload = getPayload();
        for (AddToListItem item : payload) {
            final Map<String, String> eventParams = new HashMap<>();
            eventParams.put("payload_id", getPayloadId());
            eventParams.put("tracking_id", item.getTrackingId());
            eventParams.put("item_name", item.getTitle());
            eventParams.put("source", (this instanceof DeeplinkContent) ? "deeplink" : "payload");

            AppEventTrackingManager.registerEvent(AppEventSource.SDK, "addit_added_to_list", eventParams);
        }
    }

    public void duplicate() {
        final Map<String, String> eventParams = new HashMap<>();
        eventParams.put("payload_id", getPayloadId());

        AppEventTrackingManager.registerEvent(AppEventSource.SDK, "addit_duplicate_payload", eventParams);
    }

    public void failed(String message) {
        final Map<String, String> eventParams = new HashMap<>();
        eventParams.put("payload_id", getPayloadId());

        AppErrorTrackingManager.registerEvent("ADDIT_CONTENT_FAILED", message, eventParams);
    }

    public String getPayloadId() {
        return payloadId;
    }

    public String getMessage() {
        return message;
    }

    public String getImage() {
        return image;
    }

    public int getType() {
        return type;
    }

    public List<AddToListItem> getPayload() {
        return payload;
    }
}
