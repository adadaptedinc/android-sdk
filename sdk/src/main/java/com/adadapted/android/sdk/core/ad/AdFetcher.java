package com.adadapted.android.sdk.core.ad;

import com.adadapted.android.sdk.core.session.model.Session;
import com.adadapted.android.sdk.core.zone.model.Zone;

import org.json.JSONObject;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by chrisweeden on 4/1/15.
 */
public class AdFetcher {
    private static final String LOGTAG = AdFetcher.class.getName();

    private final Set<AdFetcherListener> listeners;

    private final AdAdapter adAdapter;
    private final AdRequestBuilder requestBuilder;
    private final AdRefreshBuilder refreshBuilder;

    public AdFetcher(final AdAdapter adAdapter,
                     final AdRequestBuilder requestBuilder,
                     final AdRefreshBuilder refreshBuilder) {
        this.listeners = new HashSet<>();

        this.adAdapter = adAdapter;

        this.requestBuilder = requestBuilder;
        this.refreshBuilder = refreshBuilder;
    }

    public void fetchAdsFor(final Session session) {
        final JSONObject json = requestBuilder.buildAdRequest(session);
        adAdapter.getAds(json, new AdAdapterListener() {
            @Override
            public void onSuccess(final JSONObject adJson) {
                final Map<String, Zone> zones = refreshBuilder.buildRefreshedAds(adJson);
                notifyOnAdsRefreshed(zones);
            }

            @Override
            public void onFailure() {
                notifyOnAdsNotRefreshed();
            }
        });
    }

    public void addListener(final AdFetcherListener listener) {
        listeners.add(listener);
    }

    public void removeListener(final AdFetcherListener listener) {
        listeners.remove(listener);
    }

    private void notifyOnAdsRefreshed(final Map<String, Zone> zones) {
        for(final AdFetcherListener listener : listeners) {
            listener.onSuccess(zones);
        }
    }

    private void notifyOnAdsNotRefreshed() {
        for(final AdFetcherListener listener : listeners) {
            listener.onFailure();
        }
    }
}
