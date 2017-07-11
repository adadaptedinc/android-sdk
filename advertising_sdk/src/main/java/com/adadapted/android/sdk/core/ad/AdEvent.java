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

    private final String appId;
    private final String udid;
    private final String sessionId;
    private final String adId;
    private final String impressionId;
    private final String eventType;
    private final long datetime;
    private final String sdkVersion;

    AdEvent(final String appId,
            final String udid,
            final String sessionId,
            final String adId,
            final String impressionId,
            final String eventType,
            final String sdkVersion) {
        this.appId = appId;
        this.udid = udid;
        this.sessionId = sessionId;
        this.adId = adId;
        this.impressionId = impressionId;
        this.eventType = eventType;
        this.datetime = (new Date()).getTime();
        this.sdkVersion = sdkVersion;
    }

    public String getAppId() {
        return appId;
    }

    public String getUdid() {
        return udid;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getAdId() {
        return adId;
    }

    public String getImpressionId() {
        return impressionId;
    }

    public String getZoneId() {
        return "";
    }

    public String getEventType() {
        return eventType;
    }

    public long getDatetime() {
        return datetime;
    }

    public String getSdkVersion() {
        return sdkVersion;
    }
}
