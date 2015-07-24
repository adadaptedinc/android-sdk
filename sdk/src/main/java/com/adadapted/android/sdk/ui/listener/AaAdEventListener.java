package com.adadapted.android.sdk.ui.listener;

/**
 * Created by chrisweeden on 7/24/15.
 */
public interface AaAdEventListener {
    void onAdImpression(String zoneId);
    void onAdClick(String zoneId);
}
