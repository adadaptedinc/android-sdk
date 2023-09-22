package com.adadapted.android.sdk.core.view

import android.app.Activity
import android.content.Intent
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.RelativeLayout
import com.adadapted.android.sdk.constants.EventStrings
import com.adadapted.android.sdk.core.ad.Ad
import com.adadapted.android.sdk.core.event.EventClient
import com.adadapted.android.sdk.core.log.AALogger
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer

class AndroidWebViewPopupActivity : Activity() {
    fun createActivity(context: Context, ad: Ad): Intent {
        val intent = Intent(context.applicationContext, AndroidWebViewPopupActivity::class.java)
        intent.putExtra(EXTRA_POPUP_AD, Json.encodeToString(serializer(), ad))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        return intent
    }

    private lateinit var popupWebView: WebView
    private lateinit var ad: Ad

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val webViewLayout = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.MATCH_PARENT
        )
        popupWebView = WebView(this)
        popupWebView.layoutParams = webViewLayout

        val popupLayout = RelativeLayout(this)
        popupLayout.addView(popupWebView)
        setContentView(popupLayout)
        title = "Featured"

        val serializedAd = intent.getStringExtra(EXTRA_POPUP_AD)
        ad = serializedAd?.let { Json.decodeFromString<Ad>(it) } ?: Ad()

        val url = ad.actionPath
        if (url.startsWith("http")) {
            loadPopup(ad.actionPath)
        } else {
            EventClient.trackSdkError(
                EventStrings.POPUP_URL_MALFORMED,
                "Incorrect Action Path URL supplied for Ad: " + ad.id
            )
        }
    }

    public override fun onStart() {
        super.onStart()
        EventClient.trackPopupBegin(ad)
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
        popupWebView.addJavascriptInterface(JavascriptBridge(ad), "AdAdapted")
        popupWebView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView,
                request: WebResourceRequest
            ): Boolean {
                return false
            }

            override fun onReceivedError(
                view: WebView,
                request: WebResourceRequest,
                error: WebResourceError
            ) {
                super.onReceivedError(view, request, error)
                AALogger.logError("onReceivedError: $request $error")
                val params: MutableMap<String, String> = HashMap()
                params["url"] = url
                params["error"] = error.description.toString()
                EventClient.trackSdkError(
                    EventStrings.POPUP_URL_LOAD_FAILED,
                    "Problem loading popup url",
                    params
                )
            }
        }
        popupWebView.loadUrl(url)
    }

    companion object {
        private const val EXTRA_POPUP_AD = "AndroidWebViewPopupActivity.EXTRA_POPUP_AD"
    }
}