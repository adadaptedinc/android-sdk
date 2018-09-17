package com.adadapted.android.sdk.core.ad;

public class AdDisplayType {
    private static final String HTML = "html";

    public static boolean isValidType(final String type) {
        return HTML.equals(type);
    }
}
