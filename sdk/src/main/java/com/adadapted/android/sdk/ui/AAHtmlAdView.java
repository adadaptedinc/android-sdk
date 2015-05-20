package com.adadapted.android.sdk.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.LinearLayout;

import com.adadapted.android.sdk.core.ad.Ad;
import com.adadapted.android.sdk.core.ad.HtmlAdType;

/**
 * Created by chrisweeden on 5/20/15.
 */
class AAHtmlAdView extends WebView {
    private static final String TAG = AAHtmlAdView.class.getName();

    public AAHtmlAdView(Context context) {
        super(context);
    }

    public AAHtmlAdView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AAHtmlAdView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public AAHtmlAdView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public AAHtmlAdView(Context context, AttributeSet attrs, int defStyleAttr, boolean privateBrowsing) {
        super(context, attrs, defStyleAttr, privateBrowsing);
        init();
    }

    private void init() {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        setLayoutParams(layoutParams);
    }

    void loadHtml(Ad ad) {
        HtmlAdType adType = (HtmlAdType) ad.getAdType();
        loadUrl(adType.getAdUrl());
    }
}
