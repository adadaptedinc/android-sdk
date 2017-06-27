package com.adadapted.android.sdk.ui.view;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import com.adadapted.android.sdk.core.ad.model.AdType;
import com.adadapted.android.sdk.core.device.DeviceInfo;
import com.adadapted.android.sdk.ext.management.DeviceInfoManager;
import com.adadapted.android.sdk.ui.model.ViewAdWrapper;

class AdViewBuilder implements AdViewBuildingStrategy.Listener {
    @SuppressWarnings("unused")
    private static final String LOGTAG = AdViewBuilder.class.getName();

    public interface Listener {
        void onViewLoaded(View v);
        void onViewLoadFailed();
    }

    private Listener mListener;
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    private final EmptyAdViewStrategy mAdEmptyView;
    private ImageAdViewBuildingStrategy mAdImageView;
    private final HtmlAdViewBuildingStrategy mAdWebView;

    private AdViewBuildingStrategy mStrategy;

    AdViewBuilder(final Context context) {
        DeviceInfoManager.getInstance().getDeviceInfo(new DeviceInfoManager.Callback() {
            @Override
            public void onDeviceInfoCollected(final DeviceInfo deviceInfo) {
                mAdImageView = new ImageAdViewBuildingStrategy(context.getApplicationContext(), deviceInfo);
            }
        });

        mAdEmptyView = new EmptyAdViewStrategy(context.getApplicationContext());

        // For whatever reason the WebView has to be created ahead of time.
        // The App will likely crash if it is constructed on-demand.
        mAdWebView = new HtmlAdViewBuildingStrategy(context.getApplicationContext());
    }

    void buildView(final ViewAdWrapper currentAd,
                   final AaZoneViewProperties zoneProperties,
                   final int width,
                   final int height) {
        if(currentAd == null || zoneProperties == null) {
            return;
        }

        mStrategy = null;
        switch(currentAd.getAdType()) {
            case AdType.HTML:
                mStrategy = mAdWebView;
                break;

            case AdType.IMAGE:
                mStrategy = mAdImageView;
                break;

            default:
                mStrategy = mAdEmptyView;
        }

        mStrategy.setListener(this);
        loadView(mStrategy, currentAd, width, height, zoneProperties);
    }

    private void loadView(final AdViewBuildingStrategy strategy,
                          final ViewAdWrapper currentAd,
                          final int width,
                          final int height,
                          final AaZoneViewProperties zoneProperties) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if(strategy != null) {
                    strategy.buildView(currentAd.getAd(), width, height, zoneProperties);
                } else {
                    notifyViewLoadFailed();
                }
            }
        });
    }

    public void setListener(final Listener listener) {
        mListener = listener;
    }

    public void removeListener(final Listener listener) {
        if(mListener != null && mListener.equals(listener)) {
            mListener = null;
        }
    }

    private void notifyViewLoaded(final View v) {
        if(mListener != null) {
            mListener.onViewLoaded(v);
        }
    }

    private void notifyViewLoadFailed() {
        if(mListener != null) {
            mListener.onViewLoadFailed();
        }
    }

    public void onStrategyViewLoaded() {
        mStrategy.removeListener();
        notifyViewLoaded(mStrategy.getView());
    }

    @Override
    public void onStrategyViewLoadFailed() {
        mStrategy.removeListener();
        notifyViewLoadFailed();
    }
}
