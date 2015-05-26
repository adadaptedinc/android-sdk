package com.adadapted.android.sdk.core.ad;

/**
 * Created by chrisweeden on 4/9/15.
 */
class PopupAdAction extends AdAction {
    private boolean hideBanner;
    private String title;
    private String backgroundColor;
    private String textColor;
    private String altCloseButton;
    private String type;
    private boolean hideCloseButton;
    private boolean hideBrowserNavigation;

    public boolean shouldHideBanner() {
        return hideBanner;
    }

    public void setHideBanner(boolean hideBanner) {
        this.hideBanner = hideBanner;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public String getTextColor() {
        return textColor;
    }

    public void setTextColor(String textColor) {
        this.textColor = textColor;
    }

    public String getAltCloseButton() {
        return altCloseButton;
    }

    public void setAltCloseButton(String altCloseButton) {
        this.altCloseButton = altCloseButton;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean shouldHideCloseButton() {
        return hideCloseButton;
    }

    public void setHideCloseButton(boolean hideCloseButton) {
        this.hideCloseButton = hideCloseButton;
    }

    public boolean shouldHideBrowserNavigation() {
        return hideBrowserNavigation;
    }

    public void setHideBrowserNavigation(boolean hideBrowserNavigation) {
        this.hideBrowserNavigation = hideBrowserNavigation;
    }

    @Override
    public String toString() {
        return "AdPopup{" +
                "hideBanner=" + hideBanner +
                ", title='" + title + '\'' +
                ", backgroundColor='" + backgroundColor + '\'' +
                ", textColor='" + textColor + '\'' +
                ", altCloseButton='" + altCloseButton + '\'' +
                ", type='" + type + '\'' +
                ", hideCloseButton=" + hideCloseButton +
                ", hideBrowserNavigation=" + hideBrowserNavigation +
                '}';
    }
}
