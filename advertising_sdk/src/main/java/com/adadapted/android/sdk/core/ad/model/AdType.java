package com.adadapted.android.sdk.core.ad.model;

import java.io.Serializable;

public abstract class AdType implements Serializable {
    private static final long serialVersionUID = 42L;

    public static final String HTML = "html";
    public static final String IMAGE = "image";
    public static final String JSON = "json";
    public static final String NULL = "null";

    private String adType = NULL;

    public String getType() {
        return adType;
    }

    public void setType(final String adType) {
        this.adType = adType;
    }
}
