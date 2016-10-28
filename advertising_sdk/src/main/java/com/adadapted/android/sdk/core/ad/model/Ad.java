package com.adadapted.android.sdk.core.ad.model;

import java.io.Serializable;

/**
 * Created by chrisweeden on 3/23/15.
 */
public class Ad implements Serializable {
    private static final long serialVersionUID = 42L;

    private String adId = "";
    private String zoneId = "";
    private String baseImpressionId = "";
    private boolean hideAfterInteraction = false;
    private String trackingHtml = "";
    private int refreshTime = 0;
    private AdType adType = new NullAdType();
    private AdAction adAction = new NullAdAction();

    private boolean isHidden;
    private int impressionViews;

    public Ad() {
        isHidden = false;
        impressionViews = 0;
    }

    public String getAdId() {
        return adId;
    }

    public void setAdId(final String adId) {
        this.adId = adId;
    }

    public String getZoneId() {
        return zoneId;
    }

    public void setZoneId(final String zoneId) {
        this.zoneId = zoneId;
    }

    public String getImpressionId() {
        return baseImpressionId + ":" + impressionViews;
    }

    public void setImpressionId(final String impressionId) {
        this.baseImpressionId = impressionId;
    }

    public void incrementImpressionViews() {
        impressionViews++;
    }

    public String getTrackingHtml() {
        return trackingHtml;
    }

    public void setTrackingHtml(String trackingHtml) {
        this.trackingHtml = trackingHtml;
    }

    public int getRefreshTime() {
        return refreshTime;
    }

    public void setRefreshTime(final int refreshTime) {
        this.refreshTime = refreshTime;
    }

    public long getRefreshTimeInMs() {
        return getRefreshTime() * 1000L;
    }

    public AdType getAdType() {
        return adType;
    }

    public void setAdType(final AdType adType) {
        this.adType = adType;
    }

    public AdAction getAdAction() {
        return adAction;
    }

    public void setAdAction(final AdAction adAction) {
        this.adAction = adAction;
    }

    public boolean isHiddenAfterInteraction() {
        return hideAfterInteraction;
    }

    public void setHideAfterInteraction(final boolean hideAfterInteraction) {
        this.hideAfterInteraction = hideAfterInteraction;
    }

    public void hideAd() {
        if(isHiddenAfterInteraction()) {
            isHidden = true;
        }
    }

    public boolean isHidden() {
        return isHidden;
    }

    public boolean isNotHidden() {
        return !isHidden();
    }
}
