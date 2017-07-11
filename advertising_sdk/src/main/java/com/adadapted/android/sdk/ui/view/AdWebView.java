package com.adadapted.android.sdk.ui.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

class AdWebView extends WebView {
    private static final String LOGTAG = AdWebView.class.getName();

    interface Listener {
        void onAdLoaded();
        void onAdLoadFailed();
        void onAdClicked();
    }

    private Listener listener;

    public AdWebView(Context context, Listener listener) {
        super(context);
        this.listener = listener;
    }

    @SuppressLint("SetJavaScriptEnabled")
    public AdWebView(Context context) {
        super(context);

        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(final View v, final MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Log.i(LOGTAG, "Ad has been clicked!");
                        notifyAdClicked();
                        return true;
                }

                return false;
            }
        });
        setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(final WebView view,
                                                    final WebResourceRequest request) {
                return true;
            }

            @Override
            public void onPageFinished(WebView view,
                                       String url) {
                super.onPageFinished(view, url);
                Log.i(LOGTAG, "Displayed Ad URL: " + url);

                notifyAdLoaded();
            }

            @Override
            public void onReceivedError(WebView view,
                                        WebResourceRequest request,
                                        WebResourceError error) {
                super.onReceivedError(view, request, error);
                Log.e(LOGTAG, "Problem displaying Ad: " + error.toString());
                notifyAdLoadFailed();
            }
        });
        getSettings().setJavaScriptEnabled(true);
    }

    void notifyAdLoaded() {
        if(listener != null) {
            listener.onAdLoaded();
        }
    }

    void notifyAdLoadFailed() {
        if(listener != null) {
            listener.onAdLoadFailed();
        }
    }

    void notifyAdClicked() {
        if(listener != null) {
            listener.onAdClicked();
        }
    }
}
