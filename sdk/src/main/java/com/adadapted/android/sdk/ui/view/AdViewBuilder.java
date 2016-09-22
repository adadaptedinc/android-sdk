package com.adadapted.android.sdk.ui.view;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import com.adadapted.android.sdk.core.device.model.DeviceInfo;
import com.adadapted.android.sdk.ui.model.ViewAdWrapper;

/**
 * Created by chrisweeden on 7/1/15
 */
class AdViewBuilder implements AdViewBuildingStrategy.Listener {
    private static final String LOGTAG = AdViewBuilder.class.getName();

    public interface Listener {
        void onViewLoaded(View v);
        void onViewLoadFailed();
    }

    private final Context mContext;

    private Listener mListener;
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    private final HtmlAdViewBuildingStrategy mAdWebView;
    private AdViewBuildingStrategy mStrategy;

    public AdViewBuilder(final Context context) {
        mContext = context;

        // For whatever reason the WebView has to be created ahead of time.
        // The App will likely crash if it is constructed on-demand.
        mAdWebView = new HtmlAdViewBuildingStrategy(context, this);
    }

    public void buildView(final ViewAdWrapper currentAd,
                          final AaZoneViewProperties zoneProperties,
                          final int width,
                          final int height) {
        if(currentAd == null || zoneProperties == null) {
            return;
        }

        mStrategy = null;
        switch(currentAd.getAdType()) {
            case HTML:
                mStrategy = getHtmlViewStrategy();
                break;

            case IMAGE:
                mStrategy = getImageViewStrategy(currentAd);
                break;

            case JSON:
                mStrategy = getJsonViewStrategy();
                break;

            default:
                mStrategy = getEmptyViewStrategy();
        }

        loadView(mStrategy, currentAd, width, height, zoneProperties);
    }

    private AdViewBuildingStrategy getHtmlViewStrategy() {
        return mAdWebView;
    }

    private AdViewBuildingStrategy getImageViewStrategy(final ViewAdWrapper currentAd) {
        DeviceInfo deviceInfo = currentAd.getSession().getDeviceInfo();
        return new ImageAdViewBuildingStrategy(mContext, deviceInfo, this);
    }

    private AdViewBuildingStrategy getJsonViewStrategy() {
        return new JsonAdViewBuildingStrategy(mContext, this);
    }

    private AdViewBuildingStrategy getEmptyViewStrategy() {
        return new EmptyAdViewStrategy(mContext, this);
    }

    private void loadView(final AdViewBuildingStrategy strategy,
                          final ViewAdWrapper currentAd,
                          final int width,
                          final int height,
                          final AaZoneViewProperties zoneProperties) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                strategy.buildView(currentAd.getAd(), width, height, zoneProperties);
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
        notifyViewLoaded(mStrategy.getView());
    }

    @Override
    public void onStrategyViewLoadFailed() {
        notifyViewLoadFailed();
    }
}
