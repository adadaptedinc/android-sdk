package com.adadapted.android.sdk.ui.view;

import android.content.Context;
import android.os.Handler;
import android.view.View;

import com.adadapted.android.sdk.ui.model.ViewAdWrapper;

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
    private Handler handler = new Handler();

    private ImageAdView adImageView;
    private HtmlAdView adWebView;
    private JsonAdView adJsonView;

    public AdViewBuilder(Context context) {
        this.context = context;

        // For whatever reason the WebView has to be created ahead of time.
        // The App will likely crash if it is constructed on-demand.
        adWebView = new HtmlAdView(context, this);
    }

    public void buildView(ViewAdWrapper currentAd, int resourceId) {
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

    private void loadHtmlView(final ViewAdWrapper currentAd) {
        if(adWebView == null) {
            adWebView = new HtmlAdView(context, this);
        }

        loadView(adWebView, currentAd);
    }

    private void loadImageView(final ViewAdWrapper currentAd) {
        if(adImageView == null) {
            adImageView = new ImageAdView(context, this);
        }

        loadView(adImageView, currentAd);
    }

    private void loadJsonView(final ViewAdWrapper currentAd, int resourceId) {
        adJsonView = new JsonAdView(context, this, resourceId);
        loadView(adJsonView, currentAd);
    }

    private void loadView(final AdView view, final ViewAdWrapper currentAd) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                view.buildView(currentAd.getAd());
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

    public void onHtmlViewLoaded() {
        notifyViewLoaded(adWebView.getView());
    }

    public void onImageViewLoaded() {
        notifyViewLoaded(adImageView.getView());
    }

    public void onJsonViewLoaded() {
        notifyViewLoaded(adJsonView.getView());
    }
}
