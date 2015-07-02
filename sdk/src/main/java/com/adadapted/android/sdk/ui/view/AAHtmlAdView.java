package com.adadapted.android.sdk.ui.view;

import android.content.Context;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import com.adadapted.android.sdk.core.ad.model.Ad;
import com.adadapted.android.sdk.core.ad.model.HtmlAdType;

/**
 * Created by chrisweeden on 5/20/15.
 */
class AAHtmlAdView extends WebView {
    private static final String TAG = AAHtmlAdView.class.getName();

    public interface Listener {
        void onHtmlViewLoaded();
    }

    private Listener listener;

    public AAHtmlAdView(Listener listener, Context context) {
        super(context);

        this.listener = listener;

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        setLayoutParams(layoutParams);
        setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }
        });
    }

    void loadHtml(Ad ad) {
        HtmlAdType adType = (HtmlAdType) ad.getAdType();
        loadUrl(adType.getAdUrl());

        listener.onHtmlViewLoaded();
    }
}
