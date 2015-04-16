package com.adadapted.android.sdk.core.ad;

/**
 * Created by chrisweeden on 3/23/15.
 */
public class Ad {
    private String adId;
    private String zoneId;
    private String baseImpressionId;
    private String impressionId;
    private int refreshTime;
    private AdType adType;
    private AdAction adAction;
    private String hideAfterInteraction;
    private String payload;

    public Ad() {}

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
        return (adRefreshTime / getRefreshTimeInMs());
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
