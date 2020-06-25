package com.adadapted.android.sdk.ui.view

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.View
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient

class PixelWebView @SuppressLint("SetJavaScriptEnabled") constructor(context: Context) : WebView(context.applicationContext) {
    companion object {
        private val LOGTAG = PixelWebView::class.java.name
    }

    init {
        setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        webViewClient = object : WebViewClient() {
            override fun onReceivedError(view: WebView, request: WebResourceRequest, error: WebResourceError) {
                super.onReceivedError(view, request, error)
                Log.e(LOGTAG, "Problem loading Tracking HTML: $error")
            }
        }
        settings.javaScriptEnabled = true
    }
}