package com.adadapted.android.sdk.core.view

import android.content.Context
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.View
import android.widget.ImageButton
import android.widget.RelativeLayout
import com.adadapted.android.sdk.core.ad.Ad
import com.adadapted.android.sdk.core.ad.AdContentListener
import com.adadapted.android.sdk.core.ad.AdContentPublisher
import com.adadapted.android.sdk.core.device.DeviceInfoClient
import com.adadapted.android.sdk.core.session.SessionClient
import com.adadapted.R.drawable.report_ad

class AaZoneView : RelativeLayout, AdZonePresenterListener, AdWebView.Listener {
    interface Listener {
        fun onZoneHasAds(hasAds: Boolean)
        fun onAdLoaded()
        fun onAdLoadFailed()
    }

    private lateinit var webView: AdWebView
    private lateinit var reportButton: ImageButton
    private var presenter: AdZonePresenter = AdZonePresenter(AdViewHandler(context), SessionClient)
    private var zoneViewListener: Listener? = null
    private var isVisible = true
    private var isAdVisible = true
    private var webViewLoaded = false
    private var isAdaptiveSizingEnabled = false
    private var isFixedAspectRatioEnabled = false
    private var fixedAspectPaddingOffset = 0

    constructor(context: Context) : super(context.applicationContext) {
        setup(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context.applicationContext, attrs) {
        setup(context)
    }

    private fun setup(context: Context) {
        webView = AdWebView(context.applicationContext, this)
        reportButton = ImageButton(this.context)
        reportButton.setImageResource(report_ad)
        reportButton.setColorFilter(Color.rgb(0, 175, 204))
        reportButton.setBackgroundColor(Color.TRANSPARENT)

        if(DimensionConverter.isTablet()) {
            DimensionConverter.refreshDisplayMetrics()
        }

        val params = LayoutParams(
            LayoutParams.WRAP_CONTENT,
            LayoutParams.WRAP_CONTENT
        )

        reportButton.layoutParams = params
        params.addRule(ALIGN_PARENT_END)
        params.addRule(ALIGN_PARENT_TOP)
        params.marginEnd = 4
        params.topMargin = 4

        reportButton.setOnClickListener {
            val cachedDeviceInfo = DeviceInfoClient.getCachedDeviceInfo()
            cachedDeviceInfo?.udid?.let { udid ->
                webView.currentAd?.id?.let { it1 -> presenter.onReportAdClicked(it1, udid) }
            }
        }
        Handler(Looper.getMainLooper()).post { addView(webView) }
    }

    fun init(zoneId: String) {
        presenter.init(zoneId, webView)
    }

    fun onStart() {
        presenter.onAttach(this)
    }

    fun onStart(listener: Listener) {
        this.zoneViewListener = listener
        onStart()
    }

    fun onStart(listener: Listener, contentListener: AdContentListener) {
        AdContentPublisher.addListener(contentListener)
        onStart(listener)
    }

    fun onStart(contentListener: AdContentListener) {
        AdContentPublisher.addListener(contentListener)
        onStart()
    }

    fun setAdZoneVisibility(isViewable: Boolean) {
        isAdVisible = isViewable
        presenter.onAdVisibilityChanged(isAdVisible)
    }

    fun setAdZoneContextId(contextId: String) {
        presenter.setZoneContext(contextId)
    }

    fun removeAdZoneContext() {
        presenter.removeZoneContext()
    }

    fun clearAdZoneContext() {
        presenter.clearZoneContext()
    }

    fun onStop() {
        zoneViewListener = null
        presenter.onDetach()
    }

    fun onStop(listener: AdContentListener) {
        AdContentPublisher.removeListener(listener)
        onStop()
    }

    fun shutdown() {
        this.onStop()
    }

    fun enableAdaptiveSizing(value: Boolean) {
        isAdaptiveSizingEnabled = value
    }

    fun configureFixedAspectRatio(isEnabled: Boolean, paddingOffsetValue: Int = 0) {
        isFixedAspectRatioEnabled = isEnabled
        fixedAspectPaddingOffset = paddingOffsetValue
    }

    override fun onZoneAvailable(zone: Zone) {
        val matchParent = LayoutParams.MATCH_PARENT
        val adjustedLayoutParams = if (width == 0 || height == 0) {
            when {
                isAdaptiveSizingEnabled -> LayoutParams(matchParent, matchParent)
                isFixedAspectRatioEnabled -> {
                    val paDimensions = DimensionConverter.scaleDimensions(zone.portWidth.toInt(), zone.portHeight.toInt())
                    if (fixedAspectPaddingOffset > 0) {
                        val offSetDimens = DimensionConverter.adjustDimensionsForPadding(paDimensions.width, paDimensions.height, fixedAspectPaddingOffset)
                        LayoutParams(offSetDimens.width, offSetDimens.height)
                    } else {
                        LayoutParams(
                            paDimensions.width,
                            paDimensions.height
                        )
                    }
                }
                else -> {
                    val dimensions = zone.dimensions[Dimension.Orientation.PORT]
                    LayoutParams(
                        dimensions?.width ?: matchParent,
                        dimensions?.height ?: matchParent
                    )
                }
            }
        } else {
            LayoutParams(width, height)
        }

        Handler(Looper.getMainLooper()).post {
            webView.layoutParams = adjustedLayoutParams
            if (this.indexOfChild(reportButton) == -1) {
                addView(reportButton)
            }
        }
        notifyClientZoneHasAds(zone.hasAds())
    }

    override fun onAdsRefreshed(zone: Zone) {
        notifyClientZoneHasAds(zone.hasAds())
    }

    override fun onAdAvailable(ad: Ad) {
        loadWebViewAd(ad)
    }

    override fun onNoAdAvailable() {
        Handler(Looper.getMainLooper()).post { webView.loadBlank() }
    }

    override fun onAdVisibilityChanged(ad: Ad) {
        if(!webViewLoaded) {
            loadWebViewAd(ad)
        }
    }

    override fun onAdLoadedInWebView(ad: Ad) {
        presenter.onAdDisplayed(ad, isAdVisible)
        notifyClientAdLoaded()
    }

    override fun onAdLoadInWebViewFailed() {
        presenter.onAdDisplayFailed()
        notifyClientAdLoadFailed()
    }

    override fun onAdInWebViewClicked(ad: Ad) {
        presenter.onAdClicked(ad)
    }

    override fun onBlankAdInWebViewLoaded() {
        presenter.onBlankDisplayed()
    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)
        when (visibility) {
            View.GONE -> setInvisible()
            View.INVISIBLE -> setInvisible()
            View.VISIBLE -> setVisible()
        }
    }

    private fun loadWebViewAd(ad: Ad) {
        if (isVisible && isAdVisible && !webViewLoaded) {
            webViewLoaded = true
            Handler(Looper.getMainLooper()).post { webView.loadAd(ad) }
        } else if (isVisible && webViewLoaded) {
            Handler(Looper.getMainLooper()).post { webView.loadAd(ad) }
        }
    }

    private fun notifyClientZoneHasAds(hasAds: Boolean) {
        Handler(Looper.getMainLooper()).post {
            zoneViewListener?.onZoneHasAds(hasAds)
        }
    }

    private fun notifyClientAdLoaded() {
        Handler(Looper.getMainLooper()).post {
            zoneViewListener?.onAdLoaded()
        }
    }

    private fun notifyClientAdLoadFailed() {
        Handler(Looper.getMainLooper()).post {
            zoneViewListener?.onAdLoadFailed()
        }
    }

    private fun setVisible() {
        isVisible = true
        presenter.onAttach(this)
    }

    private fun setInvisible() {
        isVisible = false
        presenter.onDetach()
    }
}
