package com.adadapted.android.sdk.core.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import com.adadapted.android.sdk.core.ad.Ad

@SuppressLint("ClickableViewAccessibility", "SetJavaScriptEnabled", "ViewConstructor")
internal class AndroidWebView constructor(context: Context, private val listener: Listener) :
    WebView(context.applicationContext) {
    internal interface Listener {
        fun onAdLoadedInWebView(ad: Ad)
        fun onAdLoadInWebViewFailed()
        fun onAdInWebViewClicked(ad: Ad)
        fun onBlankAdInWebViewLoaded()
    }

    lateinit var currentAd: Ad
    private var loaded = false

    fun loadAd(ad: Ad) {
        currentAd = ad
        loaded = false
        loadUrl(currentAd.url)
    }

    fun loadBlank() {
        currentAd = Ad()
        val dummyDocument =
            "<html><head><meta name=\"viewport\" content=\"width=device-width, user-scalable=no\" /></head><body></body></html>"
        loadData(dummyDocument, "text/html", null)
        notifyBlankLoaded()
    }

    private fun notifyAdLoaded() {
        listener.onAdLoadedInWebView(currentAd)
    }

    private fun notifyAdLoadFailed() {
        listener.onAdLoadInWebViewFailed()
    }

    private fun notifyBlankLoaded() {
        listener.onBlankAdInWebViewLoaded()
    }

    private fun notifyAdClicked() {
        listener.onAdInWebViewClicked(currentAd)
    }

    init {
        setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        setBackgroundColor(Color.TRANSPARENT)

        setOnTouchListener(OnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_MOVE -> return@OnTouchListener true
                MotionEvent.ACTION_UP -> {
                    if (currentAd.id.isNotEmpty()) {
                        notifyAdClicked()
                    }
                    return@OnTouchListener true
                }
            }
            false
        })
        webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView,
                request: WebResourceRequest
            ): Boolean {
                return true
            }

            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                if (currentAd.id.isNotEmpty() && !loaded) {
                    loaded = true
                    notifyAdLoaded()
                }
            }

            override fun onReceivedError(
                view: WebView,
                request: WebResourceRequest,
                error: WebResourceError
            ) {
                super.onReceivedError(view, request, error)
                if (currentAd.id.isNotEmpty() && !loaded) {
                    loaded = true
                    notifyAdLoadFailed()
                }
            }
        }
        settings.javaScriptEnabled = true
    }
}