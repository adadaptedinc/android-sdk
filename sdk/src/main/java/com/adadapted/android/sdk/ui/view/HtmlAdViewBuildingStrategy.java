package com.adadapted.android.sdk.ui.view;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.adadapted.android.sdk.core.ad.model.Ad;
import com.adadapted.android.sdk.core.ad.model.HtmlAdType;

/**
 * Created by chrisweeden on 5/20/15.
 */
class HtmlAdViewBuildingStrategy implements AdViewBuildingStrategy {
    private static final String TAG = HtmlAdViewBuildingStrategy.class.getName();

    private final Listener listener;
    private final WebView view;

    public HtmlAdViewBuildingStrategy(final Context context, final Listener listener) {
        this.listener = listener;

        view = new WebView(context);
        view.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        view.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return true;
            }
        });

        String dummyDocument = "<html><head><meta name=\"viewport\" content=\"width=device-width, user-scalable=no\" /><style>body{width:100px;height100px;}</style></head><body></body></html>";
        view.loadData(dummyDocument, "text/html", null);
    }

    @Override
    public View getView() {
        return view;
    }

    @Override
    public void buildView(Ad ad, int width, int height) {
        final HtmlAdType adType = (HtmlAdType) ad.getAdType();

        if(adType.getAdUrl().toLowerCase().startsWith("http")) {
            view.setLayoutParams(new ViewGroup.LayoutParams(width, height));
            view.loadUrl(adType.getAdUrl());

            listener.onStrategyViewLoaded();
        }
        else {
            listener.onStrategyViewLoadFailed();
        }
    }

    @Override
    public void buildView(Ad ad, int width, int height, int resourceId) {
        buildView(ad, width, height);
    }

    @Override
    public String toString() {
        return "HtmlAdViewBuildingStrategy{}";
    }
}
