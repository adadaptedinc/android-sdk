package com.adadapted.android.sdk.core.ad.model;

public class HtmlAdType extends AdType {
    private String adUrl = "";

    public HtmlAdType() {
        this.setType(HTML);
    }

    public String getAdUrl() {
        return adUrl;
    }

    public void setAdUrl(final String adUrl) {
        this.adUrl = (adUrl == null) ? "" : adUrl;
    }
}
