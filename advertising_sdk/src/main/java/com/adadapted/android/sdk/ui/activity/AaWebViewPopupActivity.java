package com.adadapted.android.sdk.ui.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import com.adadapted.android.sdk.core.ad.Ad;
import com.adadapted.android.sdk.core.ad.AdEventClient;
import com.adadapted.android.sdk.core.event.AppEventClient;

import java.util.HashMap;
import java.util.Map;

public class AaWebViewPopupActivity extends Activity {
    @SuppressWarnings("unused")
    private static final String LOGTAG = AaWebViewPopupActivity.class.getName();

    private static final String EXTRA_POPUP_AD = AaWebViewPopupActivity.class.getName() + ".EXTRA_POPUP_AD";

    public static Intent createActivity(final Context context,
                                        final Ad ad) {
        Intent intent = new Intent(context.getApplicationContext(), AaWebViewPopupActivity.class);
        intent.putExtra(AaWebViewPopupActivity.EXTRA_POPUP_AD, ad);

        return intent;
    }

    private WebView popupWebView;
    private Ad ad;

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
        setTitle("Featured");

        final Intent intent = getIntent();
        ad = intent.getParcelableExtra(EXTRA_POPUP_AD);

        final String url = ad.getActionPath();
        if(url.startsWith("http")) {
            loadPopup(ad.getActionPath());
        } else {
            AppEventClient.trackError(
                "POPUP_URL_MALFORMED",
                "Incorrect Action Path URL supplied for Ad: " + ad.getId()
            );
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        AdEventClient.trackPopupBegin(ad);
    }

    public void onPause() {
        super.onPause();
        AdEventClient.trackPopupEnd(ad);
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
        popupWebView.addJavascriptInterface(new PopupJavascriptBridge(ad), "AdAdapted");
        popupWebView.setWebViewClient(new WebViewClient() {
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


                Log.w(LOGTAG, "onReceivedError: " + request.toString() + " " + error.toString());

                final Map<String, String> params = new HashMap<>();
                params.put("url", url);
                params.put("error", error.toString());
                AppEventClient.trackError(
                    "POPUP_URL_LOAD_FAILED",
                    "Problem loading popup url",
                    params
                );
            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onReceivedHttpError(WebView view,
                                            WebResourceRequest request,
                                            WebResourceResponse errorResponse) {
                super.onReceivedHttpError(view, request, errorResponse);

                Log.w(LOGTAG, "onReceivedHttpError: " + errorResponse.getStatusCode() + " " + errorResponse.getReasonPhrase());

                final Map<String, String> params = new HashMap<>();
                params.put("url", url);
                params.put("error", errorResponse.getReasonPhrase());
                AppEventClient.trackError(
                    "POPUP_URL_LOAD_FAILED",
                    "Problem loading popup url",
                    params
                );
            }
        });
        popupWebView.loadUrl(url);
    }
}
