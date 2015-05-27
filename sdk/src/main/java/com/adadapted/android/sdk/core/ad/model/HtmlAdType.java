package com.adadapted.android.sdk.core.ad.model;

/**
 * Created by chrisweeden on 4/15/15.
 */
public class HtmlAdType extends AdType {
    private String adUrl = "";

    public HtmlAdType() {
        this.setType(AdTypes.HTML);
    }

    public String getAdUrl() {
        return adUrl;
    }

    public void setAdUrl(String adUrl) {
        this.adUrl = adUrl;
    }
}
