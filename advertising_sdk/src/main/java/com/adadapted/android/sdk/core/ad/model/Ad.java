package com.adadapted.android.sdk.core.ad.model;

import java.io.Serializable;

public class Ad implements Serializable {
    private static final long serialVersionUID = 42L;

    private final String adId;
    private final String zoneId;
    private final String baseImpressionId;
    private final boolean hideAfterInteraction;
    private final String trackingHtml;
    private final int refreshTime;
    private final AdType adType;
    private final AdAction adAction;

    private boolean isHidden;
    private int impressionViews;

    private Ad(final String adId,
               final String zoneId,
               final String baseImpressionId,
               final boolean hideAfterInteraction,
               final String trackingHtml,
               final int refreshTime,
               final AdType adType,
               final AdAction adAction) {
        this.adId = adId;
        this.zoneId = zoneId;
        this.baseImpressionId = baseImpressionId;
        this.hideAfterInteraction = hideAfterInteraction;
        this.trackingHtml = trackingHtml;
        this.refreshTime = refreshTime;
        this.adType = adType;
        this.adAction = adAction;

        isHidden = false;
        impressionViews = 0;
    }

    public String getAdId() {
        return adId;
    }

    public String getZoneId() {
        return zoneId;
    }

    public String getImpressionId() {
        return baseImpressionId + ":" + impressionViews;
    }

    public void incrementImpressionViews() {
        impressionViews++;
    }

    public String getTrackingHtml() {
        return trackingHtml;
    }

    public int getRefreshTime() {
        return refreshTime;
    }

    public long getRefreshTimeInMs() {
        return getRefreshTime() * 1000L;
    }

    public AdType getAdType() {
        return adType;
    }

    public AdAction getAdAction() {
        return adAction;
    }

    public boolean isHiddenAfterInteraction() {
        return hideAfterInteraction;
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

    public static class Builder {
        private String adId;
        private String zoneId;
        private String baseImpressionId;
        private boolean hideAfterInteraction;
        private String trackingHtml;
        private int refreshTime;
        private AdType adType;
        private AdAction adAction;

        public Builder() {
            adId = "";
            zoneId = "";
            baseImpressionId = "";
            hideAfterInteraction = false;
            trackingHtml = "";
            refreshTime = 0;
            adType = new NullAdType();
            adAction = new NullAdAction();
        }

        public String getAdId() {
            return adId;
        }

        public Builder setAdId(final String adId) {
            this.adId = adId;

            return this;
        }

        public Builder setZoneId(final String zoneId) {
            this.zoneId = zoneId;

            return this;
        }

        public Builder setBaseImpressionId(final String baseImpressionId) {
            this.baseImpressionId = baseImpressionId;

            return this;
        }

        public Builder setHideAfterInteraction(boolean hideAfterInteraction) {
            this.hideAfterInteraction = hideAfterInteraction;

            return this;
        }

        public Builder setTrackingHtml(final String trackingHtml) {
            this.trackingHtml = trackingHtml;

            return this;
        }

        public Builder setRefreshTime(int refreshTime) {
            this.refreshTime = refreshTime;

            return this;
        }

        public Builder setAdType(final AdType adType) {
            this.adType = adType;

            return this;
        }

        public Builder setAdAction(final AdAction adAction) {
            this.adAction = adAction;

            return this;
        }

        public Ad build() {
            return new Ad(adId,
                    zoneId,
                    baseImpressionId,
                    hideAfterInteraction,
                    trackingHtml,
                    refreshTime,
                    adType,
                    adAction);
        }
    }
}
