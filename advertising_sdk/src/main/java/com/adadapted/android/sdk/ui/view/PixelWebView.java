package com.adadapted.android.sdk.ui.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

class PixelWebView extends WebView {
    private static final String LOGTAG = PixelWebView.class.getName();

    @SuppressLint("SetJavaScriptEnabled")
    public PixelWebView(Context context) {
        super(context.getApplicationContext());

        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedError(WebView view,
                                        WebResourceRequest request,
                                        WebResourceError error) {
                super.onReceivedError(view, request, error);
                Log.e(LOGTAG, "Problem loading Tracking HTML: " + error.toString());
            }
        });
        getSettings().setJavaScriptEnabled(true);
    }
}
