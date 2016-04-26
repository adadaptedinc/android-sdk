package com.adadapted.android.sdk.ui.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import com.adadapted.android.sdk.core.ad.model.Ad;
import com.adadapted.android.sdk.core.ad.model.PopupAdAction;
import com.adadapted.android.sdk.core.session.SessionManager;
import com.adadapted.android.sdk.core.session.model.Session;
import com.adadapted.android.sdk.ext.factory.EventTrackerFactory;
import com.adadapted.android.sdk.ext.factory.SessionManagerFactory;
import com.adadapted.android.sdk.ui.model.ViewAdWrapper;

public class AaWebViewPopupActivity extends AppCompatActivity {
    private static final String LOGTAG = AaWebViewPopupActivity.class.getName();

    public static final String EXTRA_POPUP_AD = AaWebViewPopupActivity.class.getName() + ".EXTRA_POPUP_AD";

    public static Intent createActivity(Context context, ViewAdWrapper ad) {
        Intent intent = new Intent(context, AaWebViewPopupActivity.class);
        intent.putExtra(AaWebViewPopupActivity.EXTRA_POPUP_AD, ad.getAd());

        return intent;
    }

    private WebView popupWebView;
    private Ad ad;
    private Session mSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("Web View Popup Activity");

        RelativeLayout.LayoutParams webViewLayout = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);

        popupWebView = new WebView(this);
        popupWebView.setLayoutParams(webViewLayout);

        RelativeLayout popupLayout = new RelativeLayout(this);
        popupLayout.addView(popupWebView);

        setContentView(popupLayout);

        Intent intent = getIntent();
        ad = (Ad)intent.getSerializableExtra(EXTRA_POPUP_AD);

        mSession = new Session();
        SessionManager sessionManager = SessionManagerFactory.getSessionManager();
        if(sessionManager != null) {
            mSession = sessionManager.getCurrentSession();
        }

        PopupAdAction action = (PopupAdAction)ad.getAdAction();

        loadPopup(action.getActionPath());
        styleActivity(action);
    }

    @Override
    public void onStart() {
        super.onStart();

        EventTrackerFactory.getEventTracker().trackPopupBeginEvent(mSession, ad);
    }

    public void onPause() {
        super.onPause();
        EventTrackerFactory.getEventTracker().trackPopupEndEvent(mSession, ad);
    }

    @SuppressLint({"AddJavascriptInterface", "SetJavaScriptEnabled"})
    private void loadPopup(String url) {
        popupWebView.getSettings().setJavaScriptEnabled(true);
        popupWebView.addJavascriptInterface(new WebAppInterface(this), "OldAdAdapted");
        popupWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }
        });
        popupWebView.loadUrl(url);
    }

    private void styleActivity(PopupAdAction action) {
        setTitle(action.getTitle());

        ActionBar bar = getSupportActionBar();
        try {
            setTitleColor(Color.parseColor(action.getTextColor()));
        } catch (Exception ex) {
            Log.w(LOGTAG, "Problem setting text color " + action.getTextColor());
        }

        try {
            if(bar != null) {
                bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor(action.getBackgroundColor())));
            }
        } catch (Exception ex) {
            Log.w(LOGTAG, "Problem setting background color " + action.getBackgroundColor());
        }
    }
}
