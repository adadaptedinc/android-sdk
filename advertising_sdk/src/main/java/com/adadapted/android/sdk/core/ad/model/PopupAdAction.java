package com.adadapted.android.sdk.core.ad.model;

/**
 * Created by chrisweeden on 4/9/15.
 */
public class PopupAdAction extends AdAction {
    private String  title = "Unknown";
    private String  backgroundColor = "Unknown";
    private String  textColor = "Unknown";
    private String  altCloseButton = "Unknown";
    private String  type = "Unknown";
    private boolean hideBanner = false;
    private boolean hideCloseButton = false;
    private boolean hideBrowserNavigation = false;

    public PopupAdAction() {
        super(POPUP);
    }

    public boolean shouldHideBanner() {
        return hideBanner;
    }

    public void setHideBanner(final boolean hideBanner) {
        this.hideBanner = hideBanner;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(final String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public String getTextColor() {
        return textColor;
    }

    public void setTextColor(final String textColor) {
        this.textColor = textColor;
    }

    public String getAltCloseButton() {
        return altCloseButton;
    }

    public void setAltCloseButton(final String altCloseButton) {
        this.altCloseButton = altCloseButton;
    }

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public boolean shouldHideCloseButton() {
        return hideCloseButton;
    }

    public void setHideCloseButton(final boolean hideCloseButton) {
        this.hideCloseButton = hideCloseButton;
    }

    public boolean shouldHideBrowserNavigation() {
        return hideBrowserNavigation;
    }

    public void setHideBrowserNavigation(final boolean hideBrowserNavigation) {
        this.hideBrowserNavigation = hideBrowserNavigation;
    }
}
