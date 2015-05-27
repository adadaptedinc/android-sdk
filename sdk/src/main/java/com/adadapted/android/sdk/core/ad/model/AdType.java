package com.adadapted.android.sdk.core.ad.model;

/**
 * Created by chrisweeden on 4/15/15.
 */
public abstract class AdType {
    private AdTypes adType = AdTypes.NULL;

    public AdTypes getType() {
        return adType;
    }

    public void setType(AdTypes adType) {
        this.adType = adType;
    }
}
