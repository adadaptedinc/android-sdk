package com.adadapted.android.sdk.core.ad;

import com.adadapted.android.sdk.core.device.model.DeviceInfo;
import com.adadapted.android.sdk.core.session.model.Session;
import com.adadapted.android.sdk.core.zone.model.Zone;

import org.json.JSONObject;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by chrisweeden on 4/1/15.
 */
public class AdFetcher implements AdAdapter.Listener {
    private static final String TAG = AdFetcher.class.getName();

    private final Set<Listener> listeners;

    public interface Listener {
        void onAdsRefreshed(Map<String, Zone> zones);
        void onAdsNotRefreshed();
    }

    private final AdAdapter adAdapter;
    private final AdRequestBuilder requestBuilder;
    private final AdRefreshBuilder refreshBuilder;

    public AdFetcher(AdAdapter adAdapter,
                     AdRequestBuilder requestBuilder,
                     AdRefreshBuilder refreshBuilder) {
        this.listeners = new HashSet<>();

        this.adAdapter = adAdapter;
        this.adAdapter.addListener(this);

        this.requestBuilder = requestBuilder;
        this.refreshBuilder = refreshBuilder;
    }

    public void fetchAdsFor(DeviceInfo deviceInfo, Session session) {
        JSONObject json = requestBuilder.buildAdRequest(deviceInfo, session);
        adAdapter.getAds(json);
    }

    @Override
    public void onAdGetRequestCompleted(JSONObject adJson) {
        Map<String, Zone> zones = refreshBuilder.buildRefreshedAds(adJson);
        notifyOnAdsRefreshed(zones);
    }

    @Override
    public void onAdGetRequestFailed() {
        notifyOnAdsNotRefreshed();
    }

    public void addListener(Listener listener) {
        listeners.add(listener);
    }

    public void removeListener(Listener listener) {
        listeners.remove(listener);
    }

    private void notifyOnAdsRefreshed(Map<String, Zone> zones) {
        for(Listener listener : listeners) {
            listener.onAdsRefreshed(zones);
        }
    }

    private void notifyOnAdsNotRefreshed() {
        for(Listener listener : listeners) {
            listener.onAdsNotRefreshed();
        }
    }
}
