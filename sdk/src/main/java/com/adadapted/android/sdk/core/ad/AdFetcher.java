package com.adadapted.android.sdk.core.ad;

import com.adadapted.android.sdk.core.device.DeviceInfo;
import com.adadapted.android.sdk.core.session.Session;

import org.json.JSONObject;

/**
 * Created by chrisweeden on 4/1/15.
 */
public class AdFetcher implements AdAdapter.Listener {
    private static final String TAG = AdFetcher.class.getName();

    private final AdAdapter adAdapter;

    public AdFetcher(AdAdapter adAdapter) {
        this.adAdapter = adAdapter;
        this.adAdapter.addListener(this);
    }

    public void fetchAdsFor(DeviceInfo deviceInfo, Session session) {
        JSONObject json = new AdRequestBuilder().buildAdRequestJson(deviceInfo, session);
        adAdapter.getAds(json);
    }

    @Override
    public void onAdGetRequestCompleted(JSONObject adJson) {

    }
}
