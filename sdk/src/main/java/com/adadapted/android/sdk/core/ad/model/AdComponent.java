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

    public void setCta1(String cta1) {
        this.cta1 = cta1;
    }

    public String getCta2() {
        return cta2;
    }

    public void setCta2(String cta2) {
        this.cta2 = cta2;
    }

    public String getCampaignImage() {
        return campaignImage;
    }

    public void setCampaignImage(String campaignImage) {
        this.campaignImage = campaignImage;
    }

    public String getSponsorLogo() {
        return sponsorLogo;
    }

    public void setSponsorLogo(String sponsorLogo) {
        this.sponsorLogo = sponsorLogo;
    }

    public String getSponsorName() {
        return sponsorName;
    }

    public void setSponsorName(String sponsorName) {
        this.sponsorName = sponsorName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTagLine() {
        return tagLine;
    }

    public void setTagLine(String tagLine) {
        this.tagLine = tagLine;
    }

    public String getLongText() {
        return longText;
    }

    public void setLongText(String longText) {
        this.longText = longText;
    }

    public String getSponsorText() {
        return sponsorText;
    }

    public void setSponsorText(String sponsorText) {
        this.sponsorText = sponsorText;
    }

    public String getAppIcon1() {
        return appIcon1;
    }

    public void setAppIcon1(String appIcon1) {
        this.appIcon1 = appIcon1;
    }

    public String getAppIcon2() {
        return appIcon2;
    }

    public void setAppIcon2(String appIcon2) {
        this.appIcon2 = appIcon2;
    }
}
