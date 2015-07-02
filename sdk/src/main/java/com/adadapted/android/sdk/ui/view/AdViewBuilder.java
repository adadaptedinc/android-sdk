package com.adadapted.android.sdk.ui.view;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.adadapted.android.sdk.ui.model.ViewAd;

/**
 * Created by chrisweeden on 7/1/15.
 */
public class AdViewBuilder implements AAHtmlAdView.Listener, AAImageAdView.Listener, AAJsonAdView.Listener {
    private static final String TAG = AdViewBuilder.class.getName();

    public interface Listener {
        void onViewLoaded(View v);
    }

    private Context context;
    private Listener listener;

    private AAImageAdView adImageView;
    private AAHtmlAdView adWebView;
    private AAJsonAdView adJsonView;

    public AdViewBuilder(Context context) {
        this.context = context;
    }

    public void buildView(ViewAd currentAd, int resourceId) {
        Log.d(TAG, "Building a new Ad View");

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

    private void loadHtmlView(ViewAd currentAd) {
        if(adWebView == null) {
            Log.d(TAG, "Creating a new Web View");
            adWebView = new AAHtmlAdView(this, context);
        }

        adWebView.loadHtml(currentAd.getAd());
    }

    private void loadImageView(ViewAd currentAd) {
        if(adImageView == null) {
            Log.d(TAG, "Creating a new Image View");
            adImageView = new AAImageAdView(this, context);
        }

        adImageView.loadImage(currentAd.getAd());
    }

    private void loadJsonView(ViewAd currentAd, int resourceId) {
        if(adJsonView == null) {
            Log.d(TAG, "Creating a new JSON View");
            adJsonView = new AAJsonAdView(this, context, resourceId);
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
