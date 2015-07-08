package com.adadapted.android.sdk.ui.view;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import com.adadapted.android.sdk.core.ad.model.Ad;
import com.adadapted.android.sdk.core.ad.model.HtmlAdType;

/**
 * Created by chrisweeden on 5/20/15.
 */
class HtmlAdView extends WebView implements AdView {
    private static final String TAG = HtmlAdView.class.getName();

    public interface Listener {
        void onHtmlViewLoaded();
    }

    private final Listener listener;
    private AdInteractionListener adInteractionListener;

    public HtmlAdView(final Context context, final Listener listener) {
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
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        notifyOnClick();
                        break;
                }

                return true;
            }
        });
    }

    @Override
    public void buildView(Ad ad) {
        final HtmlAdType adType = (HtmlAdType) ad.getAdType();
        Log.d(TAG, "Loading URL: " + adType.getAdUrl());

        post(new Runnable() {
            @Override
            public void run() {
                loadUrl(adType.getAdUrl());
            }
        });

        listener.onHtmlViewLoaded();
    }

    @Override
    public void setAdInteractionListener(AdInteractionListener listener) {
        adInteractionListener = listener;
    }

    @Override
    public void removeAdInteractionListener() {
        adInteractionListener = null;
    }

    private void notifyOnClick() {
        if(adInteractionListener != null) {
            adInteractionListener.onClick();
        }
    }
}
