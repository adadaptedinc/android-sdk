package com.adadapted.android.sdk.core.ad.model;

import java.io.Serializable;

/**
 * Created by chrisweeden on 4/15/15.
 */
public abstract class AdType implements Serializable {
    static final long serialVersionUID = 42L;

    private AdTypes adType = AdTypes.NULL;

    public AdTypes getType() {
        return adType;
    }

    public void setType(AdTypes adType) {
        this.adType = adType;
    }
}