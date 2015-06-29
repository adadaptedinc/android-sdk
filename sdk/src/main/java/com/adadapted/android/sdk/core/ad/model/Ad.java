package com.adadapted.android.sdk.core.ad.model;

import java.io.Serializable;

/**
 * Created by chrisweeden on 3/23/15.
 */
public class Ad implements Serializable {
    static final long serialVersionUID = 42L;

    private String adId = "";
    private String zoneId = "";
    private String baseImpressionId = "";
    private String impressionId = "";
    private boolean hideAfterInteraction = false;
    private String payload = "";
    private int refreshTime = 0;
    private AdType adType = new NullAdType();
    private AdAction adAction = new NullAdAction();

    private boolean isHidden;

    public Ad() {
        isHidden = false;
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
        this.baseImpressionId = impressionId;
    }

    public void setImpressionViews(int impressionViews) {
        impressionId = baseImpressionId + ":" + impressionViews;
    }

    public long getMaxImpressions(long adRefreshTime) {
        return (adRefreshTime / getRefreshTimeInMs()) * 2;
    }

    public int getRefreshTime() {
        return refreshTime;
    }

    public int getRefreshTimeInSec() {
        return refreshTime;
    }

    public long getRefreshTimeInMs() {
        return refreshTime * 1000L;
    }

    public void setRefreshTime(int refreshTime) {
        this.refreshTime = refreshTime;
    }

    public AdType getAdType() {
        return adType;
    }

    public void setAdType(AdType adType) {
        this.adType = adType;
    }

    public AdAction getAdAction() {
        return adAction;
    }

    public void setAdAction(AdAction adAction) {
        this.adAction = adAction;
    }

    public boolean isHiddenAfterInteraction() {
        return hideAfterInteraction;
    }

    public void setHideAfterInteraction(boolean hideAfterInteraction) {
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

    public void hideAd() {
        if(isHiddenAfterInteraction()) {
            isHidden = true;
        }
    }

    public boolean isHidden() {
        return isHidden;
    }

    @Override
    public String toString() {
        return "Ad{" +
                "adId='" + adId + '\'' +
                ", zoneId='" + zoneId + '\'' +
                ", impressionId='" + impressionId + '\'' +
                ", refreshTime=" + refreshTime +
                ", adType=" + adType +
                ", adAction=" + adAction +
                ", hideAfterInteraction='" + hideAfterInteraction + '\'' +
                ", payload='" + payload + '\'' +
                '}';
    }
}
