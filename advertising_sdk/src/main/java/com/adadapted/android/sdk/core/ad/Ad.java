package com.adadapted.android.sdk.core.ad;

import org.json.JSONObject;

import java.io.Serializable;

public class Ad implements Serializable {
    public static class ActionTypes {
        public static final String CONTENT = "c";
        public static final String LINK = "l";
        public static final String POPUP = "p";
    }

    private final String id;
    private final String zoneId;
    private final String impressionId;
    private final String url;
    private final String actionType;
    private final String actionPath;
    private final JSONObject payload;
    private final long refreshTime;
    private final String trackingHtml;

    public Ad(final String id,
              final String zoneId,
              final String impressionId,
              final String url,
              final String actionType,
              final String actionPath,
              final JSONObject payload,
              final long refreshTime,
              final String trackingHtml) {
        this.id = id;
        this.zoneId = zoneId;
        this.impressionId = impressionId;
        this.url = url;
        this.actionType = actionType;
        this.actionPath = actionPath;
        this.payload = payload;
        this.refreshTime = refreshTime;
        this.trackingHtml = trackingHtml;
    }

    public String getId() {
        return id;
    }

    public String getZoneId() {
        return zoneId;
    }

    public String getImpressionId() {
        return impressionId;
    }

    public String getUrl() {
        return url;
    }

    public String getActionType() {
        return actionType;
    }

    public String getActionPath() {
        return actionPath;
    }

    public JSONObject getPayload() {
        return payload;
    }

    public long getRefreshTime() {
        return refreshTime;
    }

    public String getTrackingHtml() {
        return trackingHtml;
    }

    public static class Builder {
        private String adId;
        private String zoneId = "";
        private String impressionId;
        private String url;
        private String actionType;
        private String actionPath;
        private JSONObject payload;
        private long refreshTime;
        private String trackingHtml;

        public String getAdId() {
            return adId;
        }

        public void setAdId(String id) {
            this.adId = id;
        }

        public String getZoneId() {
            return zoneId;
        }

        public void setZoneId(String zoneId) {
            this.zoneId = zoneId;
        }

        public String getImpressionId() {
            return impressionId;
        }

        public void setImpressionId(String impressionId) {
            this.impressionId = impressionId;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getActionType() {
            return actionType;
        }

        public void setActionType(String actionType) {
            this.actionType = actionType;
        }

        public String getActionPath() {
            return actionPath;
        }

        public void setActionPath(String actionPath) {
            this.actionPath = actionPath;
        }

        public JSONObject getPayload() {
            return payload;
        }

        public void setPayload(JSONObject payload) {
            this.payload = payload;
        }

        public long getRefreshTime() {
            return refreshTime;
        }

        public void setRefreshTime(long refreshTime) {
            this.refreshTime = refreshTime;
        }

        public String getTrackingHtml() {
            return trackingHtml;
        }

        public void setTrackingHtml(String trackingHtml) {
            this.trackingHtml = trackingHtml;
        }

        public Ad build() {
            return new Ad(
                getAdId(),
                getZoneId(),
                getImpressionId(),
                getUrl(), getActionType(),
                getActionPath(),
                getPayload(),
                getRefreshTime(),
                getTrackingHtml()
            );
        }
    }
}
