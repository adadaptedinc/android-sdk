package com.adadapted.android.sdk.core.ad;

import com.adadapted.android.sdk.core.common.Interactor;
import com.adadapted.android.sdk.core.zone.model.Zone;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by chrisweeden on 9/29/16.
 */
public class RefreshAdsInteractor implements Interactor {
    private final RefreshAdsCommand command;
    private final AdRefreshAdapter adapter;
    private final AdRequestBuilder builder;
    private final Callback callback;

    public RefreshAdsInteractor(final RefreshAdsCommand command,
                                final AdRefreshAdapter adapter,
                                final AdRequestBuilder builder,
                                final Callback callback) {
        this.command = command;
        this.adapter = adapter;
        this.builder = builder;
        this.callback = callback;
    }

    @Override
    public void execute() {
        final JSONObject json = builder.buildAdRequest(command.getSession());
        adapter.getAds(json, new AdRefreshAdapter.Callback() {
            @Override
            public void onSuccess(final Map<String, Zone> zones) {
                callback.onAdRefreshSuccess(zones);
            }

            @Override
            public void onFailure() {
                callback.onAdRefreshFailure();
            }
        });
    }

    public interface Callback {
        void onAdRefreshSuccess(Map<String, Zone> zones);
        void onAdRefreshFailure();
    }
}
