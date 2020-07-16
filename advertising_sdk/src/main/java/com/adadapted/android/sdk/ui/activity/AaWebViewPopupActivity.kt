package com.adadapted.android.sdk.ui.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.util.Log
import android.view.KeyEvent
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.RelativeLayout
import com.adadapted.android.sdk.config.EventStrings
import com.adadapted.android.sdk.core.ad.Ad
import com.adadapted.android.sdk.core.ad.AdEventClient
import com.adadapted.android.sdk.core.event.AppEventClient

class AaWebViewPopupActivity : Activity() {

    fun createActivity(context: Context, ad: Ad): Intent {
        val intent = Intent(context.applicationContext, AaWebViewPopupActivity::class.java)
        intent.putExtra(EXTRA_POPUP_AD, ad)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        return intent
    }

    private lateinit var popupWebView: WebView
    private lateinit var ad: Ad

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val webViewLayout = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT)
        popupWebView = WebView(this)
        popupWebView.layoutParams = webViewLayout

        val popupLayout = RelativeLayout(this)
        popupLayout.addView(popupWebView)
        setContentView(popupLayout)
        title = "Featured"

        val intent = intent
        ad = intent.getParcelableExtra(EXTRA_POPUP_AD)

        val url = ad.actionPath
        if (url.startsWith("http")) {
            loadPopup(ad.actionPath)
        } else {
            AppEventClient.getInstance().trackError(EventStrings.POPUP_URL_MALFORMED, "Incorrect Action Path URL supplied for Ad: " + ad.id)
        }
    }

    public override fun onStart() {
        super.onStart()
        AdEventClient.getInstance().trackPopupBegin(ad)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && popupWebView.canGoBack()) {
            popupWebView.goBack()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    @SuppressLint("AddJavascriptInterface", "SetJavaScriptEnabled")
    private fun loadPopup(url: String?) {
        if (url == null) {
            return
        }
        popupWebView.settings.javaScriptEnabled = true
        popupWebView.addJavascriptInterface(PopupJavascriptBridge(ad), "AdAdapted")
        popupWebView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                return false
            }

            override fun onReceivedError(view: WebView, request: WebResourceRequest, error: WebResourceError) {
                super.onReceivedError(view, request, error)
                Log.w(LOGTAG, "onReceivedError: $request $error")
                val params: MutableMap<String, String> = HashMap()
                params["url"] = url
                params["error"] = error.toString()
                AppEventClient.getInstance().trackError(EventStrings.POPUP_URL_LOAD_FAILED, "Problem loading popup url", params)
            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            override fun onReceivedHttpError(view: WebView, request: WebResourceRequest, errorResponse: WebResourceResponse) {
                super.onReceivedHttpError(view, request, errorResponse)
                Log.w(LOGTAG, "onReceivedHttpError: " + errorResponse.statusCode + " " + errorResponse.reasonPhrase)
                val params: MutableMap<String, String> = HashMap()
                params["url"] = url
                params["error"] = errorResponse.reasonPhrase
                AppEventClient.getInstance().trackError(EventStrings.POPUP_URL_LOAD_FAILED, "Problem loading popup url", params)
            }
        }
        popupWebView.loadUrl(url)
    }

    companion object {
        private val LOGTAG = AaWebViewPopupActivity::class.java.name
        private val EXTRA_POPUP_AD = AaWebViewPopupActivity::class.java.name + ".EXTRA_POPUP_AD"
    }
}