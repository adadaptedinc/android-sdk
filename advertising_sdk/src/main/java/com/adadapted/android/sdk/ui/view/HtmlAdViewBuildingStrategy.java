package com.adadapted.android.sdk.ui.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import com.adadapted.android.sdk.core.ad.model.Ad;
import com.adadapted.android.sdk.core.ad.model.HtmlAdType;

class HtmlAdViewBuildingStrategy implements AdViewBuildingStrategy {
    private static final String LOGTAG = HtmlAdViewBuildingStrategy.class.getName();

    private Listener mListener;
    private WebView mWebView;

    @SuppressLint("SetJavaScriptEnabled")
    HtmlAdViewBuildingStrategy(final Context context) {
        try {
            mWebView = new WebView(context.getApplicationContext());
            mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            mWebView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(final WebView view,
                                                        final String url) {
                    return true;
                }

                @Override
                public boolean shouldOverrideUrlLoading(final WebView view,
                                                        final WebResourceRequest request) {
                    return true;
                }
            });
            mWebView.getSettings().setJavaScriptEnabled(true);
        }
        catch(Throwable ex) {
            Log.e(LOGTAG, "Problem initializing HTML Ad WebView");
        }
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
        if(mWebView == null) {
            if(mListener != null) {
                mListener.onStrategyViewLoadFailed();
            }

            return;
        }

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

    public void setListener(final Listener listener) {
        mListener = listener;
    }

    public void removeListener() {
        mListener = null;
    }

    private void setDummyDocument(final int color) {
        final String hexColor = String.format("#%06X", (0xFFFFFF & color));
        final String dummyDocument = "<html><head><meta name=\"viewport\" content=\"width=device-width, user-scalable=no\" /><style>body{background-color:"+hexColor+";width:100px;height100px;}</style></head><body></body></html>";

        mWebView.loadData(dummyDocument, "text/html", null);
    }
}
