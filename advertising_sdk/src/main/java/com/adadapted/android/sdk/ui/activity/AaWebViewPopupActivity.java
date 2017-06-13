package com.adadapted.android.sdk.ui.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import com.adadapted.android.sdk.core.ad.model.Ad;
import com.adadapted.android.sdk.core.ad.model.PopupAdAction;
import com.adadapted.android.sdk.core.session.model.Session;
import com.adadapted.android.sdk.ext.management.AdEventTrackingManager;
import com.adadapted.android.sdk.ext.management.AppErrorTrackingManager;
import com.adadapted.android.sdk.ext.management.SessionManager;
import com.adadapted.android.sdk.ui.model.ViewAdWrapper;

import java.util.HashMap;
import java.util.Map;

public class AaWebViewPopupActivity extends Activity {
    private static final String LOGTAG = AaWebViewPopupActivity.class.getName();

    public static final String EXTRA_POPUP_AD = AaWebViewPopupActivity.class.getName() + ".EXTRA_POPUP_AD";

    public static Intent createActivity(final Context context,
                                        final ViewAdWrapper ad) {
        Intent intent = new Intent(context.getApplicationContext(), AaWebViewPopupActivity.class);
        intent.putExtra(AaWebViewPopupActivity.EXTRA_POPUP_AD, ad.getAd());

        return intent;
    }

    private WebView popupWebView;
    private Ad ad;
    private Session mSession;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final RelativeLayout.LayoutParams webViewLayout = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);

        popupWebView = new WebView(this);
        popupWebView.setLayoutParams(webViewLayout);

        final RelativeLayout popupLayout = new RelativeLayout(this);
        popupLayout.addView(popupWebView);

        setContentView(popupLayout);

        final Intent intent = getIntent();
        ad = (Ad)intent.getSerializableExtra(EXTRA_POPUP_AD);

        mSession = SessionManager.getCurrentSession();
        if(mSession == null) {
            mSession = new Session();
        }

        final PopupAdAction action = (PopupAdAction)ad.getAdAction();

        loadPopup(action.getActionPath());
        setTitle(action.getTitle());
    }

    @Override
    public void onStart() {
        super.onStart();
        AdEventTrackingManager.trackPopupBeginEvent(mSession, ad);
    }

    public void onPause() {
        super.onPause();
        AdEventTrackingManager.trackPopupEndEvent(mSession, ad);
    }

    @Override
    public boolean onKeyDown(final int keyCode,
                             final KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && popupWebView.canGoBack()) {
            popupWebView.goBack();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @SuppressLint({"AddJavascriptInterface", "SetJavaScriptEnabled"})
    private void loadPopup(final String url) {
        if(url == null) {
            return;
        }

        popupWebView.getSettings().setJavaScriptEnabled(true);
        popupWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(final WebView view,
                                                    final String url) {
                return false;
            }

            @Override
            public boolean shouldOverrideUrlLoading(final WebView view,
                                                    final WebResourceRequest request) {
                return false;
            }

            @Override
            public void onReceivedError(WebView view,
                                        WebResourceRequest request,
                                        WebResourceError error) {
                super.onReceivedError(view, request, error);

                final Map<String, String> params = new HashMap<>();
                params.put("url", url);
                params.put("error", error.toString());
                AppErrorTrackingManager.registerEvent(
                    "POPUP_URL_LOAD_FAILED",
                    "Problem loading popup url",
                    params
                );
            }

            @Override
            public void onReceivedHttpError(WebView view,
                                            WebResourceRequest request,
                                            WebResourceResponse errorResponse) {
                super.onReceivedHttpError(view, request, errorResponse);

                final Map<String, String> params = new HashMap<>();
                params.put("url", url);
                params.put("error", errorResponse.toString());
                AppErrorTrackingManager.registerEvent(
                    "POPUP_URL_LOAD_FAILED",
                    "Problem loading popup url",
                    params
                );
            }
        });
        popupWebView.loadUrl(url);
    }
}
