package com.adadapted.android.sdk.ui.messaging;

/**
 * Created by chrisweeden on 8/18/15.
 */
public interface AaSdkEventListener {
    void onHasAdsToServe(boolean hasAds);
    void onNextAdEvent(String zoneId, String eventType);
}
