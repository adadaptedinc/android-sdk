package com.adadapted.android.sdk.core.ad;

/**
 * Created by chrisweeden on 4/15/15.
 */
public class HtmlAdType extends AdType {
    private String adUrl;

    public HtmlAdType() {
        this.setAdType(AdTypes.HTML);
    }

    public String getAdUrl() {
        return adUrl;
    }

    public void setAdUrl(String adUrl) {
        this.adUrl = adUrl;
    }
}
