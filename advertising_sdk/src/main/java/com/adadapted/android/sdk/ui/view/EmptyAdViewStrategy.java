package com.adadapted.android.sdk.ui.view;

import android.content.Context;
import android.view.View;

import com.adadapted.android.sdk.core.ad.model.Ad;

/**
 * Created by chrisweeden on 7/22/15
 */
class EmptyAdViewStrategy implements AdViewBuildingStrategy {
    private final Listener mListener;
    private final View mView;

    EmptyAdViewStrategy(final Context context, final Listener listener) {
        mListener = listener;
        mView = new View(context);
    }

    @Override
    public View getView() {
        return mView;
    }

    @Override
    public void buildView(final Ad ad,
                          final int width,
                          final int height,
                          final AaZoneViewProperties zoneProperties) {
        if(mListener != null) {
            mListener.onStrategyViewLoadFailed();
        }
    }

    @Override
    public String toString() {
        return "EmptyAdViewStrategy{}";
    }
}
