package com.adadapted.android.sdk.ui.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.adadapted.android.sdk.core.ad.Ad;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@SuppressLint("ViewConstructor")
class AdWebView extends WebView {
    @SuppressWarnings("unused")
    private static final String LOGTAG = AdWebView.class.getName();

    interface Listener {
        void onAdLoaded(Ad ad);
        void onAdLoadFailed(Ad ad);
        void onAdClicked(Ad ad);
        void onBlankLoaded();
    }

    private final Listener listener;

    private Ad currentAd;
    private boolean loaded;
    private final Lock adLock = new ReentrantLock();

    @SuppressLint("SetJavaScriptEnabled")
    public AdWebView(final Context context,
                     final Listener listener) {
        super(context.getApplicationContext());
        this.listener = listener;

        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        setBackgroundColor(Color.TRANSPARENT);
        setOnTouchListener(new OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(final View v, final MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        return true;

                    case MotionEvent.ACTION_UP:
                        if(!currentAd.getId().isEmpty()) {
                            notifyAdClicked();
                        }
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

                adLock.lock();
                try {
                    if(!currentAd.getId().isEmpty() && !loaded) {
                        loaded = true;
                        notifyAdLoaded();
                    }
                }
                finally {
                    adLock.unlock();
                }
            }

            @Override
            public void onReceivedError(WebView view,
                                        WebResourceRequest request,
                                        WebResourceError error) {
                super.onReceivedError(view, request, error);

                if(!currentAd.getId().isEmpty() && !loaded) {
                    loaded = true;
                    notifyAdLoadFailed();
                }
            }
        });
        getSettings().setJavaScriptEnabled(true);
    }

    void loadAd(final Ad ad) {
        adLock.lock();
        try {
            currentAd = ad;
            loaded = false;
            loadUrl(currentAd.getUrl());
        }
        finally {
            adLock.unlock();
        }
    }

    void loadBlank() {
        adLock.lock();
        try {
            currentAd = new Ad();

            final String dummyDocument = "<html><head><meta name=\"viewport\" content=\"width=device-width, user-scalable=no\" /></head><body></body></html>";
            loadData(dummyDocument, "text/html", null);

            notifyBlankLoaded();
        }
        finally {
            adLock.unlock();
        }
    }

    private void notifyAdLoaded() {
        adLock.lock();
        try {
            if(listener != null) {
                listener.onAdLoaded(currentAd);
            }
        }
        finally {
            adLock.unlock();
        }
    }

    private void notifyAdLoadFailed() {
        adLock.lock();
        try {
            if(listener != null) {
                listener.onAdLoadFailed(currentAd);
            }
        }
        finally {
            adLock.unlock();
        }
    }

    private void notifyBlankLoaded() {
        adLock.lock();
        try {
            if(listener != null) {
                listener.onBlankLoaded();
            }
        }
        finally {
            adLock.unlock();
        }
    }

    private void notifyAdClicked() {
        adLock.lock();
        try {
            if(listener != null) {
                listener.onAdClicked(currentAd);
            }
        }
        finally {
            adLock.unlock();
        }
    }
}
