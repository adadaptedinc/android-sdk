package com.adadapted.android.sdk.core.ad.model;

import java.io.Serializable;

/**
 * Created by chrisweeden on 7/9/15.
 */
public class AdComponent implements Serializable {
    private static final long serialVersionUID = 42L;

    private String cta1 = "";
    private String cta2 = "";
    private String campaignImage = "";
    private String sponsorLogo = "";
    private String sponsorName = "";
    private String title = "";
    private String tagLine = "";
    private String longText = "";
    private String sponsorText = "";
    private String appIcon1 = "";
    private String appIcon2 = "";

    public String getCta1() {
        return cta1;
    }

    public void setCta1(final String cta1) {
        this.cta1 = cta1;
    }

    public String getCta2() {
        return cta2;
    }

    public void setCta2(final String cta2) {
        this.cta2 = cta2;
    }

    public String getCampaignImage() {
        return campaignImage;
    }

    public void setCampaignImage(final String campaignImage) {
        this.campaignImage = campaignImage;
    }

    public String getSponsorLogo() {
        return sponsorLogo;
    }

    public void setSponsorLogo(final String sponsorLogo) {
        this.sponsorLogo = sponsorLogo;
    }

    public String getSponsorName() {
        return sponsorName;
    }

    public void setSponsorName(final String sponsorName) {
        this.sponsorName = sponsorName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getTagLine() {
        return tagLine;
    }

    public void setTagLine(final String tagLine) {
        this.tagLine = tagLine;
    }

    public String getLongText() {
        return longText;
    }

    public void setLongText(final String longText) {
        this.longText = longText;
    }

    public String getSponsorText() {
        return sponsorText;
    }

    public void setSponsorText(final String sponsorText) {
        this.sponsorText = sponsorText;
    }

    public String getAppIcon1() {
        return appIcon1;
    }

    public void setAppIcon1(final String appIcon1) {
        this.appIcon1 = appIcon1;
    }

    public String getAppIcon2() {
        return appIcon2;
    }

    public void setAppIcon2(final String appIcon2) {
        this.appIcon2 = appIcon2;
    }
}
