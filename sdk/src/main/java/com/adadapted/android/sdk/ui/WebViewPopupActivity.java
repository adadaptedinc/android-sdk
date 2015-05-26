package com.adadapted.android.sdk.ui;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.adadapted.android.sdk.R;

public class WebViewPopupActivity extends ActionBarActivity {
    private static final String TAG = WebViewPopupActivity.class.getName();

    public static final String EXTRA_POPUP_URL = WebViewPopupActivity.class.getName() + ".EXTRA_POPUP_URL";

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view_popup);

        Intent intent = getIntent();
        String url = intent.getStringExtra(EXTRA_POPUP_URL);

        webView = (WebView)findViewById(R.id.activity_web_view_popup_webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(url);
    }
}
