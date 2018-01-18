package com.adadapted.android.sdk.core.keywordintercept;

public class Suggestion {
    private final String replacement;
    private final String icon;
    private final String tagLine;

    public Suggestion(final String replacement,
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
}
