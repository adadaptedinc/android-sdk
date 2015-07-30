package com.adadapted.android.sdk.ui.view;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import com.adadapted.android.sdk.ui.model.ViewAdWrapper;

/**
 * Created by chrisweeden on 7/1/15.
 */
class AdViewBuilder implements AdViewBuildingStrategy.Listener {
    private static final String TAG = AdViewBuilder.class.getName();

    public interface Listener {
        void onViewLoaded(View v);
        void onViewLoadFailed();
    }

    private final Context context;

    private Listener listener;
    private Handler handler = new Handler(Looper.getMainLooper());

    private HtmlAdViewBuildingStrategy adWebView;
    private AdViewBuildingStrategy strategy;

    public AdViewBuilder(Context context) {
        this.context = context;

        // For whatever reason the WebView has to be created ahead of time.
        // The App will likely crash if it is constructed on-demand.
        adWebView = new HtmlAdViewBuildingStrategy(context, this);
    }

    public void buildView(ViewAdWrapper currentAd, int resourceId, int width, int height) {
        strategy = null;
        switch(currentAd.getAdType()) {
            case HTML:
                strategy = getHtmlViewStrategy();
                break;

            case IMAGE:
                strategy = getImageViewStrategy();
                break;

            case JSON:
                strategy = getJsonViewStrategy();
                break;

            default:
                strategy = getEmptyViewStrategy();
        }

        loadView(strategy, currentAd, width, height, resourceId);
    }

    private AdViewBuildingStrategy getHtmlViewStrategy() {
        return adWebView;
    }

    private AdViewBuildingStrategy getImageViewStrategy() {
        return new ImageAdViewBuildingStrategy(context, this);
    }

    private AdViewBuildingStrategy getJsonViewStrategy() {
        return new JsonAdViewBuildingStrategy(context, this);
    }

    private AdViewBuildingStrategy getEmptyViewStrategy() {
        return new EmptyAdViewStrategy(context, this);
    }

    private void loadView(final AdViewBuildingStrategy strategy,
                          final ViewAdWrapper currentAd,
                          final int width,
                          final int height,
                          final int resourceId) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                strategy.buildView(currentAd.getAd(), width, height, resourceId);
            }
        });
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public void removeListener(Listener listener) {
        if(this.listener != null && this.listener.equals(listener)) {
            this.listener = null;
        }
    }

    public void notifyViewLoaded(View v) {
        if(listener != null) {
            listener.onViewLoaded(v);
        }
    }

    public void notifyViewLoadFailed() {
        if(listener != null) {
            listener.onViewLoadFailed();
        }
    }

    public void onStrategyViewLoaded() {
        notifyViewLoaded(strategy.getView());
    }

    @Override
    public void onStrategyViewLoadFailed() {
        notifyViewLoadFailed();
    }
}
