package com.adadapted.android.sdk.ui.view;

import android.content.Context;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import com.adadapted.android.sdk.core.ad.model.Ad;
import com.adadapted.android.sdk.core.ad.model.HtmlAdType;

/**
 * Created by chrisweeden on 5/20/15
 */
class HtmlAdViewBuildingStrategy implements AdViewBuildingStrategy {
    private static final String LOGTAG = HtmlAdViewBuildingStrategy.class.getName();

    private final Listener mListener;
    private final WebView mWebView;

    public HtmlAdViewBuildingStrategy(final Context context, final Listener listener) {
        mListener = listener;

        mWebView = new WebView(context);
        mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return true;
            }
        });
    }

    @Override
    public View getView() {
        return mWebView;
    }

    @Override
    public void buildView(final Ad ad,
                          final int width,
                          final int height,
                          final AaZoneViewProperties zoneProperties) {
        setDummyDocument(zoneProperties.getBackgroundColor());
        mWebView.setBackgroundColor(zoneProperties.getBackgroundColor());

        final HtmlAdType adType = (HtmlAdType) ad.getAdType();

        if(adType.getAdUrl().toLowerCase().startsWith("http")) {
            mWebView.setLayoutParams(new RelativeLayout.LayoutParams(width, height));
            mWebView.loadUrl(adType.getAdUrl());

            if(mListener != null) {
                mListener.onStrategyViewLoaded();
            }
        }
        else {
            if(mListener != null) {
                mListener.onStrategyViewLoadFailed();
            }
        }
    }

    private void setDummyDocument(final int color) {
        final String hexColor = String.format("#%06X", (0xFFFFFF & color));
        final String dummyDocument = "<html><head><meta name=\"viewport\" content=\"width=device-width, user-scalable=no\" /><style>body{background-color:"+hexColor+";width:100px;height100px;}</style></head><body></body></html>";

        mWebView.loadData(dummyDocument, "text/html", null);
    }

    @Override
    public String toString() {
        return "HtmlAdViewBuildingStrategy{}";
    }
}
