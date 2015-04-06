package com.adadapted.android.sdk;

import org.json.JSONObject;

/**
 * Created by chrisweeden on 4/1/15.
 */
class AdFetcher implements AdAdapter.Listener {
    private static final String TAG = AdFetcher.class.getName();

    private final AdAdapter adAdapter;

    AdFetcher(AdAdapter adAdapter) {
        this.adAdapter = adAdapter;
        this.adAdapter.addListener(this);
    }

    void fetchAdsFor(DeviceInfo deviceInfo, Session session) {
        JSONObject json = new AdRequestBuilder().buildAdRequestJson(deviceInfo, session);
        adAdapter.getAds(json);
    }

    @Override
    public void onAdGetRequestCompleted(JSONObject adJson) {

    }
}
