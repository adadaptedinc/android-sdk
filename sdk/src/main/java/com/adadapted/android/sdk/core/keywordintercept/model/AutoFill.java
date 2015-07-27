package com.adadapted.android.sdk.core.keywordintercept.model;

/**
 * Created by chrisweeden on 7/16/15.
 */
public class AutoFill {
    private final String replacement;
    private final String icon;
    private final String tagline;

    public AutoFill(String replacement, String icon, String tagline) {
        this.replacement = replacement;
        this.icon = icon;
        this.tagline = tagline;
    }

    public String getReplacement() {
        return replacement;
    }

    public String getIcon() {
        return icon;
    }

    public String getTagline() {
        return tagline;
    }

    @Override
    public String toString() {
        return "AutoFill{" +
                "replacement='" + replacement + '\'' +
                ", icon='" + icon + '\'' +
                ", tagline='" + tagline + '\'' +
                '}';
    }
}
