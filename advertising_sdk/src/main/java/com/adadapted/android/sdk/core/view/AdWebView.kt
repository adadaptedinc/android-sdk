package com.adadapted.android.sdk.core.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.MotionEvent
import android.view.View
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import com.adadapted.android.sdk.core.ad.Ad
import kotlin.math.abs

@SuppressLint("ClickableViewAccessibility", "SetJavaScriptEnabled", "ViewConstructor")
internal class AdWebView(context: Context, private val listener: Listener) :
    WebView(context.applicationContext) {
    internal interface Listener {
        fun onAdLoadedInWebView(ad: Ad)
        fun onAdLoadInWebViewFailed()
        fun onAdInWebViewClicked(ad: Ad)
        fun onBlankAdInWebViewLoaded()
    }

    var currentAd: Ad? = null
    private var loaded = false

    fun loadAd(ad: Ad) {
        currentAd = ad
        loaded = false
        currentAd?.url?.let { loadUrl(it) }
    }

    fun loadBlank() {
        currentAd = Ad()
        val dummyDocument =
            "<html><head><meta name=\"viewport\" content=\"width=device-width, user-scalable=no\" /></head><body></body></html>"
        loadData(dummyDocument, "text/html", null)
        notifyBlankLoaded()
    }

    private fun notifyAdLoaded() {
        currentAd?.let { listener.onAdLoadedInWebView(it) }
    }

    private fun notifyAdLoadFailed() {
        listener.onAdLoadInWebViewFailed()
    }

    private fun notifyBlankLoaded() {
        listener.onBlankAdInWebViewLoaded()
    }

    private fun notifyAdClicked() {
        currentAd?.let { listener.onAdInWebViewClicked(it) }
    }

    init {
        setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        setBackgroundColor(Color.TRANSPARENT)

        setOnTouchListener(object : OnTouchListener {
            var isMoved = false
            private var startX = 0f
            private var startY = 0f
            private val moveThreshold = 20f

            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                when (event?.action) {
                    MotionEvent.ACTION_DOWN -> {
                        isMoved = false
                        startX = event.x
                        startY = event.y
                        return true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        val deltaX = abs(event.x - startX)
                        val deltaY = abs(event.y - startY)
                        if (deltaX > moveThreshold || deltaY > moveThreshold) {
                            isMoved = true
                        }
                        return true
                    }
                    MotionEvent.ACTION_UP -> {
                        //Only accept the click if no significant move event was detected
                        if (!isMoved && currentAd?.id?.isNotEmpty() == true) {
                            notifyAdClicked()
                        }
                        return true
                    }
                }
                return false
            }
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
                if (currentAd?.id?.isNotEmpty() == true && !loaded) {
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
                if (currentAd?.id?.isNotEmpty() == true && !loaded) {
                    loaded = true
                    notifyAdLoadFailed()
                }
            }
        }
        settings.javaScriptEnabled = true
    }
}