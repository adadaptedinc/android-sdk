package com.adadapted.android.sdk.core.view

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.ViewGroup
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.adadapted.R
import com.adadapted.android.sdk.core.ad.Ad
import com.adadapted.android.sdk.core.ad.AdContentListener
import com.adadapted.android.sdk.core.ad.AdContentPublisher
import com.adadapted.android.sdk.core.device.DeviceInfoClient
import com.adadapted.android.sdk.core.session.SessionClient

class AdadaptedComposable(context: Context): AdZonePresenterListener {
    interface Listener {
        fun onZoneHasAds(hasAds: Boolean)
        fun onAdLoaded()
        fun onAdLoadFailed()
    }
    private var presenter: AdZonePresenter = AdZonePresenter(AdViewHandler(context), SessionClient)
    private var storedContentListener: AdContentListener? = null
    private var zoneViewListener: Listener? = null
    private var isAdVisible = true
    private var webView = AdWebView(context, object : AdWebView.Listener {
        override fun onAdInWebViewClicked(ad: Ad) {
            presenter.onAdClicked(ad)
        }

        override fun onAdLoadInWebViewFailed() {
            presenter.onAdDisplayFailed()
            notifyClientAdLoadFailed()
        }

        override fun onAdLoadedInWebView(ad: Ad) {
            presenter.onAdDisplayed(ad, isAdVisible)
            notifyClientAdLoaded()
        }

        override fun onBlankAdInWebViewLoaded() {
            presenter.onBlankDisplayed()
        }
    }).apply { currentAd = Ad() }

    @Composable
    fun CustomZoneView(modifier: Modifier) {
        InternalZoneView(modifier = modifier)
    }

    @Composable
    fun ZoneView() {
        InternalZoneView(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .padding(start = 4.dp, end = 4.dp)
        )
    }

    @Composable
    private fun InternalZoneView(modifier: Modifier) {
        Box(modifier = modifier) {
            AndroidView(factory = {
                webView
            })
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.report_ad),
                contentDescription = "Report Ad",
                tint = Color(red = 80, green = 188, blue = 236),
                modifier = Modifier
                    .size(18.dp)
                    .align(Alignment.TopEnd)
                    .padding(top = 4.dp, end = 4.dp)
                    .clickable {
                        val cachedDeviceInfo = DeviceInfoClient.getCachedDeviceInfo()
                        cachedDeviceInfo?.udid?.let { udid ->
                            presenter.onReportAdClicked(webView.currentAd.id, udid)
                        }
                    }
            )
        }
        DisposableEffect(key1 = this) {
            onDispose {
                dispose()
            }
        }
    }

    fun init(zoneId: String, zoneListener: Listener?, contentListener: AdContentListener?) {
        presenter.init(zoneId)
        storedContentListener = contentListener
        zoneViewListener = zoneListener
        if (contentListener != null) {
            AdContentPublisher.addListener(contentListener)
        }
        presenter.onAttach(this)
        webView.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
    }

    fun setAdZoneVisibility(isViewable: Boolean) {
        isAdVisible = isViewable
        presenter.onAdVisibilityChanged(isAdVisible)
    }

    fun setAdZoneContextId(contextId: String) {
        presenter.setZoneContext(contextId)
    }

    fun clearAdZoneContext() {
        presenter.clearZoneContext()
    }

    private fun dispose() {
        storedContentListener?.let { AdContentPublisher.removeListener(it) }
        storedContentListener = null
        zoneViewListener = null
        presenter.onDetach()
    }

    override fun onAdAvailable(ad: Ad) {
        Handler(Looper.getMainLooper()).post { webView.loadAd(ad) }
    }

    override fun onAdsRefreshed(zone: Zone) {
        notifyClientZoneHasAds(zone.hasAds())
    }

    override fun onNoAdAvailable() {
        Handler(Looper.getMainLooper()).post { webView.loadBlank() }
    }

    override fun onZoneAvailable(zone: Zone) {
        notifyClientZoneHasAds(zone.hasAds())
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
}