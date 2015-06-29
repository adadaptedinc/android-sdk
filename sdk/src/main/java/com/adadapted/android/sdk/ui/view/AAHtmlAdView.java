package com.adadapted.android.sdk.ui.view;

import android.content.Context;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import com.adadapted.android.sdk.core.ad.model.Ad;
import com.adadapted.android.sdk.core.ad.model.HtmlAdType;
import com.adadapted.android.sdk.ui.listener.AdViewListenable;
import com.adadapted.android.sdk.ui.listener.AdViewListener;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by chrisweeden on 5/20/15.
 */
class AAHtmlAdView extends WebView implements AdViewListenable {
    private static final String TAG = AAHtmlAdView.class.getName();

    private Set<AdViewListener> listeners;

    public AAHtmlAdView(Context context) {
        super(context);
        initView();
    }

    private void initView() {
        listeners = new HashSet<>();

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

        notifyOnViewLoaded();
    }

    public void addListener(AdViewListener listener) {
        listeners.add(listener);
    }

    public void removeListener(AdViewListener listener) {
        listeners.remove(listener);
    }

    private void notifyOnViewLoaded() {
        for(AdViewListener listener : listeners) {
            listener.onViewLoaded();
        }
    }
}
