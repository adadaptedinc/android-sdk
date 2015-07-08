package com.adadapted.android.sdk.ui.view;

import android.content.Context;
import android.view.View;

import com.adadapted.android.sdk.ui.model.ViewAd;

/**
 * Created by chrisweeden on 7/1/15.
 */
class AdViewBuilder implements HtmlAdView.Listener, ImageAdView.Listener, JsonAdView.Listener {
    private static final String TAG = AdViewBuilder.class.getName();

    public interface Listener {
        void onViewLoaded(View v);
    }

    private final Context context;

    private Listener listener;

    private ImageAdView adImageView;
    private HtmlAdView adWebView;
    private JsonAdView adJsonView;

    public AdViewBuilder(Context context) {
        this.context = context;
    }

    public void buildView(ViewAd currentAd, int resourceId) {
        resetListeners();

        switch(currentAd.getAdType()) {
            case HTML:
                loadHtmlView(currentAd);
                break;

            case IMAGE:
                loadImageView(currentAd);
                break;

            case JSON:
                loadJsonView(currentAd, resourceId);
                break;

            default:
                notifyViewLoaded(new View(context));
        }
    }

    private void resetListeners() {
        if(adImageView != null) {
            adImageView.removeAdInteractionListener();
        }

        if(adWebView != null) {
            adWebView.removeAdInteractionListener();
        }

        if(adJsonView != null) {
            adJsonView.removeAdInteractionListener();
        }
    }

    private void loadHtmlView(ViewAd currentAd) {
        if(adWebView == null) {
            adWebView = new HtmlAdView(context, this);
        }

        adWebView.buildView(currentAd.getAd());
    }

    private void loadImageView(ViewAd currentAd) {
        if(adImageView == null) {
            adImageView = new ImageAdView(context, this);
        }

        adImageView.buildView(currentAd.getAd());
    }

    private void loadJsonView(ViewAd currentAd, int resourceId) {
        if(adJsonView == null) {
            adJsonView = new JsonAdView(context, this, resourceId);
        }

        adJsonView.buildView(currentAd.getAd());
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

    public void onHtmlViewLoaded() {
        notifyViewLoaded(adWebView);
    }

    public void onImageViewLoaded() {
        notifyViewLoaded(adImageView);
    }

    public void onJsonViewLoaded() {
        notifyViewLoaded(adJsonView);
    }
}
