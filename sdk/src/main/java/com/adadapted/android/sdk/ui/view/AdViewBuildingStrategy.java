package com.adadapted.android.sdk.ui.view;

import android.view.View;

import com.adadapted.android.sdk.core.ad.model.Ad;

/**
 * Created by chrisweeden on 7/7/15.
 */
interface AdViewBuildingStrategy {
    void buildView(Ad ad);
    View getView();
}
