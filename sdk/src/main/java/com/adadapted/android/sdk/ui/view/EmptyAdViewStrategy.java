package com.adadapted.android.sdk.ui.view;

import android.content.Context;
import android.view.View;

import com.adadapted.android.sdk.core.ad.model.Ad;

/**
 * Created by chrisweeden on 7/22/15.
 */
class EmptyAdViewStrategy implements AdViewBuildingStrategy {
    private Listener mListener;
    private View mView;

    EmptyAdViewStrategy(Context context, Listener listener) {
        mListener = listener;
        mView = new View(context);
    }

    @Override
    public View getView() {
        return mView;
    }

    @Override
    public void buildView(Ad ad, int width, int height) {
        mListener.onStrategyViewLoadFailed();
    }

    @Override
    public void buildView(Ad ad, int width, int height, int resourceId) {
        buildView(ad, width, height);
    }

    @Override
    public String toString() {
        return "EmptyAdViewStrategy{}";
    }
}
