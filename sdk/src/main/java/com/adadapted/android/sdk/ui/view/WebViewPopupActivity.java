package com.adadapted.android.sdk.ui.view;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.adadapted.android.sdk.R;
import com.adadapted.android.sdk.core.ad.model.Ad;
import com.adadapted.android.sdk.core.ad.model.PopupAdAction;

public class WebViewPopupActivity extends AppCompatActivity {
    private static final String TAG = WebViewPopupActivity.class.getName();

    public static final String EXTRA_POPUP_AD = WebViewPopupActivity.class.getName() + ".EXTRA_POPUP_AD";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view_popup);

        Intent intent = getIntent();
        Ad ad = (Ad)intent.getSerializableExtra(EXTRA_POPUP_AD);
        PopupAdAction action = (PopupAdAction)ad.getAdAction();

        loadPopup(action.getActionPath());
        styleActivity(action);
    }

    private void loadPopup(String url) {
        WebView webView = (WebView)findViewById(R.id.activity_web_view_popup_webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new WebAppInterface(this), "AdAdapted");
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }
        });
        webView.loadUrl(url);
    }

    private void styleActivity(PopupAdAction action) {
        setTitle(action.getTitle());

        ActionBar bar = getSupportActionBar();
        try {
            setTitleColor(Color.parseColor(action.getTextColor()));
        } catch (Exception ex) {
            Log.d(TAG, "Problem setting text color " + action.getTextColor(), ex);
        }

        try {
            bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor(action.getBackgroundColor())));
        } catch (NullPointerException ex) {
            Log.d(TAG, "Problem setting background color " + action.getBackgroundColor(), ex);
        }
    }
}
