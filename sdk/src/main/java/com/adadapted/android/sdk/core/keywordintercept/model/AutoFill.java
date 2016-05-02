package com.adadapted.android.sdk.core.keywordintercept.model;

/**
 * Created by chrisweeden on 7/16/15.
 */
public class AutoFill {
    private final String replacement;
    private final String icon;
    private final String tagLine;

    public AutoFill(final String replacement,
                    final String icon,
                    final String tagLine) {
        this.replacement = replacement;
        this.icon = icon;
        this.tagLine = tagLine;
    }

    public String getReplacement() {
        return replacement;
    }

    public String getIcon() {
        return icon;
    }

    public String getTagLine() {
        return tagLine;
    }

    @Override
    public String toString() {
        return "AutoFill{" +
                "replacement='" + replacement + '\'' +
                ", icon='" + icon + '\'' +
                ", tagLine='" + tagLine + '\'' +
                '}';
    }
}
