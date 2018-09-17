package com.adadapted.android.sdk.core.ad;

public class AdActionType {
    public static final String CONTENT = "c";
    public static final String CONTENT_POPUP = "cp";
    public static final String POPUP = "p";

    public static boolean isValidType(final String type) {
        return CONTENT.equals(type) || CONTENT_POPUP.equals(type) || POPUP.equals(type);
    }

    public static boolean handlesContent(final String type) {
        return CONTENT.equals(type) || CONTENT_POPUP.equals(type);
    }
}
