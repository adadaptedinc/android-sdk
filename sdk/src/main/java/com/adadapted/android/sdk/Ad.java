package com.adadapted.android.sdk;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by chrisweeden on 3/23/15.
 */
class Ad {
    private String adId;
    private String zoneId;
    private String impressionId;
    private int refreshTime;
    private String adType;
    private String actionType;
    private String actionPath;
    private String hideAfterInteraction;
    private String payload;

    private Map<String, AdImage> images;

    public Ad() {
        images = new HashMap<>();
    }

    public String getAdId() {
        return adId;
    }

    public void setAdId(String adId) {
        this.adId = adId;
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

    public int getRefreshTime() {
        return refreshTime;
    }

    public void setRefreshTime(int refreshTime) {
        this.refreshTime = refreshTime;
    }

    public String getAdType() {
        return adType;
    }

    public void setAdType(String adType) {
        this.adType = adType;
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

    public String getHideAfterInteraction() {
        return hideAfterInteraction;
    }

    public void setHideAfterInteraction(String hideAfterInteraction) {
        this.hideAfterInteraction = hideAfterInteraction;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public boolean hasPayload() {
        return (payload != null);
    }

    public Map<String, AdImage> getImages() {
        return images;
    }

    public AdImage getStandardImages() {
        return images.get("standard");
    }

    public AdImage getRetinaImages() {
        return images.get("retina");
    }

    public void setImages(Map<String, AdImage> images) {
        this.images = images;
    }

    @Override
    public String toString() {
        return "Ad{" +
                "adId='" + adId + '\'' +
                ", zoneId='" + zoneId + '\'' +
                ", impressionId='" + impressionId + '\'' +
                ", refreshTime=" + refreshTime +
                ", adType='" + adType + '\'' +
                ", actionType='" + actionType + '\'' +
                ", actionPath='" + actionPath + '\'' +
                ", hideAfterInteraction='" + hideAfterInteraction + '\'' +
                ", payload='" + payload + '\'' +
                ", images=" + images +
                '}';
    }
}
