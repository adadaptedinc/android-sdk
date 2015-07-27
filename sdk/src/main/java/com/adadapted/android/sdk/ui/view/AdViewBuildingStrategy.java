package com.adadapted.android.sdk.ui.view;

import android.view.View;

import com.adadapted.android.sdk.core.ad.model.Ad;

/**
 * Created by chrisweeden on 7/7/15.
 */
interface AdViewBuildingStrategy {
    interface Listener {
        void onStrategyViewLoaded();
    }

    void buildView(Ad ad, int width, int height);
    void buildView(Ad ad, int width, int height, int resourceId);
    View getView();
}
