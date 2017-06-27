package com.adadapted.android.sdk.ui.view;

import android.view.View;

import com.adadapted.android.sdk.core.ad.model.Ad;

interface AdViewBuildingStrategy {
    interface Listener {
        void onStrategyViewLoaded();
        void onStrategyViewLoadFailed();
    }

    void buildView(Ad ad, int width, int height, AaZoneViewProperties zoneProperties);
    View getView();

    void setListener(final Listener listener);
    void removeListener();
}
