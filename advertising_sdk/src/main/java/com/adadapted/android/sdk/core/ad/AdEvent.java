package com.adadapted.android.sdk.core.ad;

import java.util.Date;

public class AdEvent {
    public static class Types {
        public static final String IMPRESSION = "impression";
        public static final String IMPRESSION_END = "impression_end";
        public static final String INTERACTION = "interaction";
        public static final String POPUP_BEGIN = "popup_begin";
        public static final String POPUP_END = "popup_end";
        public static final String CUSTOM = "custom";
    }

    private final String adId;
    private final String zoneId;
    private final String impressionId;
    private final String eventType;
    private final long createdAt;

    AdEvent(final String adId,
            final String zoneId,
            final String impressionId,
            final String eventType) {
        this.adId = adId;
        this.zoneId = zoneId;
        this.impressionId = impressionId;
        this.eventType = eventType;
        this.createdAt = (new Date()).getTime();
    }

    public String getAdId() {
        return adId;
    }

    public String getImpressionId() {
        return impressionId;
    }

    public String getZoneId() {
        return zoneId;
    }

    public String getEventType() {
        return eventType;
    }

    public long getCreatedAt() {
        return createdAt;
    }
}
