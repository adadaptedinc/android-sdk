package com.adadapted.android.sdk.ui.view

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
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock

@SuppressLint("ViewConstructor")
internal class AdWebView @SuppressLint("SetJavaScriptEnabled") constructor(context: Context, private val listener: Listener) : WebView(context.applicationContext) {
    internal interface Listener {
        fun onAdLoaded(ad: Ad)
        fun onAdLoadFailed()
        fun onAdClicked(ad: Ad)
        fun onBlankLoaded()
    }

    private lateinit var currentAd: Ad
    private var loaded = false
    private val adLock: Lock = ReentrantLock()

    fun loadAd(ad: Ad) {
        adLock.lock()
        try {
            currentAd = ad
            loaded = false
            loadUrl(currentAd.url)
        } finally {
            adLock.unlock()
        }
    }

    fun loadBlank() {
        adLock.lock()
        try {
            currentAd = Ad()
            val dummyDocument = "<html><head><meta name=\"viewport\" content=\"width=device-width, user-scalable=no\" /></head><body></body></html>"
            loadData(dummyDocument, "text/html", null)
            notifyBlankLoaded()
        } finally {
            adLock.unlock()
        }
    }

    private fun notifyAdLoaded() {
        adLock.lock()
        try {
            listener.onAdLoaded(currentAd)
        } finally {
            adLock.unlock()
        }
    }

    private fun notifyAdLoadFailed() {
        adLock.lock()
        try {
            listener.onAdLoadFailed()
        } finally {
            adLock.unlock()
        }
    }

    private fun notifyBlankLoaded() {
        adLock.lock()
        try {
            listener.onBlankLoaded()
        } finally {
            adLock.unlock()
        }
    }

    private fun notifyAdClicked() {
        adLock.lock()
        try {
            listener.onAdClicked(currentAd)
        } finally {
            adLock.unlock()
        }
    }

    companion object {
        private val LOGTAG = AdWebView::class.java.name
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
            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                return true
            }

            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                adLock.lock()
                try {
                    if (currentAd.id.isNotEmpty() && !loaded) {
                        loaded = true
                        notifyAdLoaded()
                    }
                } finally {
                    adLock.unlock()
                }
            }

            override fun onReceivedError(view: WebView, request: WebResourceRequest, error: WebResourceError) {
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