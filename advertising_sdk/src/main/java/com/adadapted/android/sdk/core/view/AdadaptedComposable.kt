package com.adadapted.android.sdk.core.view

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.RelativeLayout.LayoutParams
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
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
    private var viewVisibilityInitialized = false
    private var isAdVisible = true
    private var contextId = ""
    private var isFixedAspectRatioEnabled = false
    private var fixedAspectPaddingOffset = 0
    private var webViewLoaded = false
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
    fun ZoneView(
        zoneId: String,
        zoneListener: Listener?,
        contentListener: AdContentListener?,
        isZoneVisible: MutableState<Boolean> = mutableStateOf(true),
        zoneContextId: MutableState<String> = mutableStateOf(""),
        isFixedAspectRatioEnabled: Boolean = false,
        fixedAspectPaddingOffset: Int = 0,
        modifier: Modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(start = 4.dp, end = 4.dp)
    ) {
        InternalZoneView(
            modifier = modifier,
            zoneId = zoneId,
            zoneListener = zoneListener,
            contentListener = contentListener,
            isZoneVisible = isZoneVisible,
            zoneContextId = zoneContextId,
            isFixedAspectRatioEnabled = isFixedAspectRatioEnabled,
            fixedAspectPaddingOffset =  fixedAspectPaddingOffset
        )
    }

    @Composable
    private fun InternalZoneView(
        zoneId: String,
        zoneListener: Listener?,
        contentListener: AdContentListener?,
        modifier: Modifier,
        isZoneVisible: MutableState<Boolean>,
        zoneContextId: MutableState<String>,
        isFixedAspectRatioEnabled: Boolean = false,
        fixedAspectPaddingOffset: Int = 0,
    ) {
        val isInitialized = remember { mutableStateOf(false) }
        val lifecycleOwner = LocalLifecycleOwner.current

        DisposableEffect(lifecycleOwner) {
            val observer = LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_START) {
                    if (!isInitialized.value) {
                        initializeComposable(zoneId, zoneListener, contentListener, isZoneVisible, zoneContextId)
                        isInitialized.value = true
                    }
                }
            }

            lifecycleOwner.lifecycle.addObserver(observer)

            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
            }
        }

        this.isAdVisible = isZoneVisible.value
        this.isFixedAspectRatioEnabled = isFixedAspectRatioEnabled
        this.fixedAspectPaddingOffset = fixedAspectPaddingOffset

        setAdZoneVisibility(isAdVisible)
        setAdZoneContextId(contextId)

        Box(modifier = modifier) {
            AndroidView(
                factory = {
                    webView
                }
            )
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
                            webView.currentAd?.id?.let { presenter.onReportAdClicked(it, udid) }
                        }
                    }
            )
        }

        DisposableEffect(key1 = zoneId) {
            onDispose {
                dispose()
            }
        }
    }

    private fun initializeComposable(
        zoneId: String,
        zoneListener: Listener?,
        contentListener: AdContentListener?,
        isVisible: MutableState<Boolean>,
        adContextId: MutableState<String>
    ) {
        presenter.init(zoneId, webView)
        contextId = adContextId.value
        isAdVisible = isVisible.value
        if(contextId.isNotEmpty()) { setAdZoneContextId(contextId) }
        storedContentListener = contentListener
        zoneViewListener = zoneListener
        if (contentListener != null) {
            AdContentPublisher.addListener(contentListener)
        }
        presenter.onAttach(this)
    }

    private fun setAdZoneVisibility(isViewable: Boolean) {
        if (isViewable && !viewVisibilityInitialized) {
            viewVisibilityInitialized = true
        }
        isAdVisible = isViewable
        presenter.onAdVisibilityChanged(isAdVisible)
    }

    private fun setAdZoneContextId(contextId: String) {
        if (contextId.isEmpty()) {
            presenter.removeZoneContext()
        } else {
            presenter.setZoneContext(contextId)
        }
    }

    fun dispose() {
        storedContentListener?.let { AdContentPublisher.removeListener(it) }
        storedContentListener = null
        zoneViewListener = null
        presenter.onDetach()
    }

    override fun onAdAvailable(ad: Ad) {
        loadWebViewAd(ad)
    }

    override fun onAdsRefreshed(zone: Zone) {
        notifyClientZoneHasAds(zone.hasAds())
    }

    override fun onNoAdAvailable() {
        runOnMainThread { webView.loadBlank() }
    }

    override fun onAdVisibilityChanged(ad: Ad) {
        if(!webViewLoaded) {
            loadWebViewAd(ad)
        }
    }

    override fun onZoneAvailable(zone: Zone) {
        val adjustedLayoutParams: LayoutParams
        if (isFixedAspectRatioEnabled) {
            val paDimensions = zone.pixelAccurateDimensions[Dimension.Orientation.PORT]
            if (fixedAspectPaddingOffset > 0 && paDimensions != null) {
                val offSetDimens = DimensionConverter.adjustDimensionsForPadding(paDimensions.width, paDimensions.height, fixedAspectPaddingOffset)
                adjustedLayoutParams = LayoutParams(offSetDimens.width, offSetDimens.height)
            } else {
                adjustedLayoutParams = LayoutParams(
                    paDimensions?.width ?: LayoutParams.MATCH_PARENT,
                    paDimensions?.height ?: LayoutParams.MATCH_PARENT
                )
            }
        } else {
            val dimensions = zone.dimensions[Dimension.Orientation.PORT]
            adjustedLayoutParams = LayoutParams(
                dimensions?.width ?: LayoutParams.MATCH_PARENT,
                dimensions?.height ?: LayoutParams.MATCH_PARENT
            )
        }

        runOnMainThread {
            webView.layoutParams = adjustedLayoutParams
        }
        notifyClientZoneHasAds(zone.hasAds())
    }

    private fun loadWebViewAd(ad: Ad) {
        if (viewVisibilityInitialized && isAdVisible && !webViewLoaded) {
            webViewLoaded = true
            runOnMainThread { webView.loadAd(ad) }
        } else if (viewVisibilityInitialized && webViewLoaded) {
            runOnMainThread { webView.loadAd(ad) }
        }
    }

    private fun notifyClientZoneHasAds(hasAds: Boolean) {
        runOnMainThread {
            zoneViewListener?.onZoneHasAds(hasAds)
        }
    }

    private fun notifyClientAdLoaded() {
        runOnMainThread {
            zoneViewListener?.onAdLoaded()
        }
    }

    private fun notifyClientAdLoadFailed() {
        runOnMainThread {
            zoneViewListener?.onAdLoadFailed()
        }
    }

    private fun runOnMainThread(action: () -> Unit) {
        Handler(Looper.getMainLooper()).post(action)
    }
}
