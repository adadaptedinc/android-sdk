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
    private Handler mHandler = new Handler(Looper.getMainLooper());

    private HtmlAdViewBuildingStrategy mAdWebView;
    private AdViewBuildingStrategy mStrategy;

    public AdViewBuilder(Context context) {
        mContext = context;

        // For whatever reason the WebView has to be created ahead of time.
        // The App will likely crash if it is constructed on-demand.
        mAdWebView = new HtmlAdViewBuildingStrategy(context, this);
    }

    public void buildView(ViewAdWrapper currentAd, int resourceId, int width, int height) {
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

        loadView(mStrategy, currentAd, width, height, resourceId);
    }

    private AdViewBuildingStrategy getHtmlViewStrategy() {
        return mAdWebView;
    }

    private AdViewBuildingStrategy getImageViewStrategy(ViewAdWrapper currentAd) {
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
                          final int resourceId) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                strategy.buildView(currentAd.getAd(), width, height, resourceId);
            }
        });
    }

    public void setListener(Listener listener) {
        mListener = listener;
    }

    public void removeListener(Listener listener) {
        if(mListener != null && mListener.equals(listener)) {
            mListener = null;
        }
    }

    public void notifyViewLoaded(View v) {
        if(mListener != null) {
            mListener.onViewLoaded(v);
        }
    }

    public void notifyViewLoadFailed() {
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
