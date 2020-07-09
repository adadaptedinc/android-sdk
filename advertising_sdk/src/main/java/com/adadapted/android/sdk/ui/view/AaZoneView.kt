package com.adadapted.android.sdk.ui.view

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import com.adadapted.android.sdk.core.ad.Ad
import com.adadapted.android.sdk.core.common.Dimension
import com.adadapted.android.sdk.core.zone.Zone
import com.adadapted.android.sdk.ui.messaging.AdContentListener
import com.adadapted.android.sdk.ui.messaging.AdContentPublisher

class AaZoneView : RelativeLayout, AdZonePresenter.Listener, AdWebView.Listener {
    interface Listener {
        fun onZoneHasAds(hasAds: Boolean)
        fun onAdLoaded()
        fun onAdLoadFailed()
    }

    private lateinit var webView: AdWebView
    private var presenter: AdZonePresenter? = null
    private var isVisible = true
    private var listener: Listener? = null

    constructor(context: Context) : super(context.applicationContext) {
        setup(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context.applicationContext, attrs) {
        setup(context)
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context.applicationContext, attrs, defStyleAttr) {
        setup(context)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context.applicationContext, attrs, defStyleAttr, defStyleRes) {
        setup(context)
    }

    private fun setup(context: Context) {
        presenter = AdZonePresenter(context.applicationContext)
        webView = AdWebView(context.applicationContext, this)
        Handler(Looper.getMainLooper()).post { addView(webView) }
    }

    fun init(zoneId: String) {
        presenter?.init(zoneId)
    }

    @Deprecated("No need to pass in the layoutResourceId anymore", ReplaceWith("init(zoneId)"))
    fun init(zoneId: String, layoutResourceId: Int) {
        init(zoneId)
    }

    fun shutdown() {
        Handler(Looper.getMainLooper()).post { this@AaZoneView.onStop() }
    }

    /**
     * onStart()
     */
    fun onStart() {
        presenter?.onAttach(this)
    }

    fun onStart(listener: Listener) {
        this.listener = listener
        onStart()
    }

    fun onStart(listener: Listener, contentListener: AdContentListener) {
        AdContentPublisher.getInstance().addListener(contentListener)
        onStart(listener)
    }

    fun onStart(contentListener: AdContentListener) {
        AdContentPublisher.getInstance().addListener(contentListener)
        onStart()
    }

    /**
     * onStop()
     */
    fun onStop() {
        listener = null
        presenter?.onDetach()
    }

    fun onStop(listener: AdContentListener) {
        AdContentPublisher.getInstance().removeListener(listener)
        onStop()
    }

    /*
     * Notifies AaZoneView.Listener
     */
    private fun notifyZoneHasAds(hasAds: Boolean) {
        Handler(Looper.getMainLooper()).post {
            listener?.onZoneHasAds(hasAds)
        }
    }

    private fun notifyAdLoaded() {
        Handler(Looper.getMainLooper()).post {
            listener?.onAdLoaded()
        }
    }

    private fun notifyAdLoadFailed() {
        Handler(Looper.getMainLooper()).post {
            listener?.onAdLoadFailed()
        }
    }

    /*
     * Overrides from AdZonePresenter.Listener
     */
    override fun onZoneAvailable(zone: Zone) {
        var adjustedLayoutParams = LayoutParams(width, height)
        if (width == 0 || height == 0) {
            val dimension = zone.dimensions[Dimension.Orientation.PORT]
            adjustedLayoutParams = LayoutParams(dimension?.width ?: LayoutParams.MATCH_PARENT, dimension?.height ?: LayoutParams.MATCH_PARENT)
        }
        Handler(Looper.getMainLooper()).post { webView.layoutParams = adjustedLayoutParams }
        notifyZoneHasAds(zone.hasAds())
    }

    override fun onAdsRefreshed(zone: Zone) {
        notifyZoneHasAds(zone.hasAds())
    }

    override fun onAdAvailable(ad: Ad) {
        if (isVisible) {
            Handler(Looper.getMainLooper()).post { webView.loadAd(ad) }
        }
    }

    override fun onNoAdAvailable() {
        Handler(Looper.getMainLooper()).post { webView.loadBlank() }
    }

    /*
     * Overrides from AdWebView.Listener
     */
    override fun onAdLoaded(ad: Ad) {
        if (presenter != null) {
            presenter!!.onAdDisplayed(ad)
            notifyAdLoaded()
        }
    }

    override fun onAdLoadFailed() {
        if (presenter != null) {
            presenter!!.onAdDisplayFailed()
            notifyAdLoadFailed()
        }
    }

    override fun onAdClicked(ad: Ad) {
        presenter?.onAdClicked(ad)
    }

    override fun onBlankLoaded() {
        presenter?.onBlankDisplayed()
    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)
        when (visibility) {
            View.GONE -> setInvisible()
            View.INVISIBLE -> setInvisible()
            View.VISIBLE -> setVisible()
        }
    }

    private fun setVisible() {
        isVisible = true
        presenter?.onAttach(this)
    }

    private fun setInvisible() {
        isVisible = false
        presenter?.onDetach()
    }

    companion object {
        private val LOGTAG = AaZoneView::class.java.name
    }
}