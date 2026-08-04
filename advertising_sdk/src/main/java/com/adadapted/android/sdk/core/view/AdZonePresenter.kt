package com.adadapted.android.sdk.core.view

import com.adadapted.android.sdk.constants.EventStrings
import com.adadapted.android.sdk.core.ad.Ad
import com.adadapted.android.sdk.core.ad.AdActionType
import com.adadapted.android.sdk.core.ad.AdClient
import com.adadapted.android.sdk.core.ad.AdContentPublisher
import com.adadapted.android.sdk.core.ad.AdZoneData
import com.adadapted.android.sdk.core.concurrency.Timer
import com.adadapted.android.sdk.core.event.EventClient
import com.adadapted.android.sdk.core.interfaces.ZoneAdListener
import com.adadapted.android.sdk.core.log.AALogger

interface AdZonePresenterListener {
    fun onZoneAvailable(adZoneData: AdZoneData)
    fun onAdAvailable(ad: Ad)
    fun onNoAdAvailable()
    fun onAdVisibilityChanged(ad: Ad)
}

class AdZonePresenter(private val adViewHandler: AdViewHandler, private val adClient: AdClient) : ZoneAdListener {
    private var currentAd: Ad = Ad()
    private var zoneId: String = ""
    private var isZoneVisible: Boolean = true
    private var adZonePresenterListener: AdZonePresenterListener? = null
    private var attached: Boolean
    private var zoneContextId: String = ""
    private var zoneLoaded: Boolean
    private var currentAdZoneData: AdZoneData
    private var adStarted = false
    private var adCompleted = false
    private var timerRunning = false
    private lateinit var timer: Timer
    private val eventClient: EventClient = EventClient
    private var webView: AdWebView? = null

    fun init(zoneId: String, webView: AdWebView) {
        if (this.zoneId.isEmpty()) {
            this.zoneId = zoneId
        }
        this.webView = webView
    }

    fun onAttach(adZonePresenterListener: AdZonePresenterListener?) {
        if (adZonePresenterListener == null) {
            AALogger.logError("NULL Listener provided")
            return
        }
        if (!attached) {
            attached = true
            this.adZonePresenterListener = adZonePresenterListener
            if(currentAd.id.isEmpty()) { //First attach only
                adClient.fetchNewAd(zoneId, contextId = zoneContextId, listener = this) //FIRST INITIAL CALL
            }
            startZoneTimer()
        }
    }

    fun onDetach() {
        if (attached) {
            attached = false
            adZonePresenterListener = null
            completeCurrentAd()
            stopTimer()
        }
    }

    fun setZoneContext(contextId: String) {
        this.zoneContextId = contextId
        eventClient.trackRecipeContextEvent(contextId, this.zoneId)
    }

    fun removeZoneContext() {
        this.zoneContextId = ""
    }

    private fun getNextAd() {
        restartTimer()
        if (!zoneLoaded) return

        completeCurrentAd()

        adClient.fetchNewAd(
            zoneId = zoneId,
            contextId = zoneContextId,
            listener = object : ZoneAdListener {
                override fun onAdLoaded(adZoneData: AdZoneData) = handleAd(adZoneData.ad)
                override fun onAdLoadFailed() = handleAd(clearedAdKeepingRefreshTime())
            })
    }

    private fun handleAd(ad: Ad) {
        currentAd = ad
        adStarted = false
        adCompleted = false
        restartTimer() //Pick up the new Ad's refresh time
        displayAd()
    }

    private fun displayAd() {
        if (currentAd.isEmpty) {
            notifyNoAdAvailable()
        } else {
            notifyAdAvailable(currentAd)
        }
    }

    private fun completeCurrentAd() {
        if (!currentAd.isEmpty && adStarted && !adCompleted) {
            if (!isZoneVisible && !currentAd.impressionWasTracked()) {
                eventClient.trackInvisibleImpression(currentAd)
            }
            adCompleted = true
        }
    }

    fun onAdDisplayed(ad: Ad, isAdVisible: Boolean) {
        isZoneVisible = isAdVisible
        startZoneTimer()
        adStarted = true
        trackAdImpression(ad, isAdVisible)
    }

    fun onAdVisibilityChanged(isAdVisible: Boolean) {
        isZoneVisible = isAdVisible
        adZonePresenterListener?.onAdVisibilityChanged(currentAd)
        trackAdImpression(currentAd, isAdVisible)
    }

    fun onAdDisplayFailed() {
        adStarted = true
        currentAd = clearedAdKeepingRefreshTime()
        startZoneTimer()
    }

    fun onBlankDisplayed() {
        adStarted = true
        currentAd = clearedAdKeepingRefreshTime()
        startZoneTimer()
    }

    //Clears the Ad content but keeps the served refresh, which on a no-fill is the backoff the
    //server asked for and is often the value the zone timer is first armed with. Also used when a
    //refetch fails, so the zone does not fall back to the faster default against a server that
    //asked to be hit less often.
    private fun clearedAdKeepingRefreshTime() = Ad(refreshTime = currentAd.refreshTime)

    fun onAdClicked(ad: Ad) {
        val actionType = ad.actionType
        val params: MutableMap<String, String> = HashMap()
        params["id"] = ad.id

        when (actionType) {
            AdActionType.CONTENT -> {
                eventClient.trackSdkEvent(EventStrings.ATL_AD_CLICKED, params)
                handleContentAction(ad)
            }
            AdActionType.LINK, AdActionType.EXTERNAL_LINK -> {
                eventClient.trackInteraction(ad)
                handleLinkAction(ad)
            }
            AdActionType.POPUP -> {
                eventClient.trackInteraction(ad)
                handlePopupAction(ad)
            }
            AdActionType.CONTENT_POPUP -> {
                eventClient.trackSdkEvent(EventStrings.POPUP_AD_CLICKED, params)
                handlePopupAction(ad)
            }
            else -> AALogger.logError("AdZonePresenter Cannot handle Action type: $actionType")
        }

        getNextAd()
    }

    fun onReportAdClicked(adId: String, udid: String) {
        adViewHandler.handleReportAd(adId, udid)
    }

    private fun trackAdImpression(ad: Ad, isAdVisible: Boolean) {
        if (!isAdVisible || ad.impressionWasTracked() || ad.isEmpty || webView?.loaded == false) return
        callPixelTrackingJavaScript()
        eventClient.trackImpression(ad)
    }

    private fun callPixelTrackingJavaScript() {
        webView?.evaluateJavascript(PIXEL_TRACKING_JS) {}
        AALogger.logDebug("Pixel Tracking Called.")
    }

    private fun startZoneTimer() {
        if (!zoneLoaded || timerRunning) {
            return
        }
        val refreshSeconds = currentAd.refreshTimeOrDefault
        if (currentAd.refreshTimeWasRejected) {
            AALogger.logError("Ad refresh time of ${currentAd.refreshTime}s was served but not honored. Using ${refreshSeconds}s")
        }
        AALogger.logDebug("Zone timer starting with a refresh of ${refreshSeconds}s")
        timerRunning = true
        timer = Timer(
            { getNextAd() },
            repeatSeconds = refreshSeconds,
            delaySeconds = refreshSeconds
        )
    }

    private fun restartTimer() {
        if (::timer.isInitialized) {
            timer.cancelTimer()
            timerRunning = false
            startZoneTimer()
        }
    }

    private fun stopTimer() {
        if (::timer.isInitialized) {
            timer.cancelTimer()
            timerRunning = false
        }
    }

    private fun handleContentAction(ad: Ad) {
        val zoneId = ad.zoneId
        AdContentPublisher.publishContent(zoneId, ad.getContent())
    }

    private fun handleLinkAction(ad: Ad) {
        adViewHandler.handleLink(ad)
        AdContentPublisher.publishNonContentNotification(zoneId, ad.id)
    }

    private fun handlePopupAction(ad: Ad) {
        adViewHandler.handlePopup(ad)
        AdContentPublisher.publishNonContentNotification(zoneId, ad.id)
    }

    private fun notifyZoneAvailable() {
        adZonePresenterListener?.onZoneAvailable(currentAdZoneData)
    }

    private fun notifyAdAvailable(ad: Ad) {
        adZonePresenterListener?.onAdAvailable(ad)
    }

    private fun notifyNoAdAvailable() {
        AALogger.logInfo("No ad available")
        adZonePresenterListener?.onNoAdAvailable()
    }

    private fun updateCurrentZone(adZoneData: AdZoneData) {
        zoneLoaded = true
        currentAdZoneData = adZoneData
        if(DimensionConverter.isTablet()) {
            currentAdZoneData.rescaleDimensionsForTablet()
        }
        handleAd(adZoneData.ad)
    }

    override fun onAdLoaded(adZoneData: AdZoneData) {
        if (zoneId.isEmpty()) {
            AALogger.logError("AdZoneId is empty. Was onStop() called outside the host view's overriding function?")
        }
        updateCurrentZone(adZoneData)
        notifyZoneAvailable()
    }

    override fun onAdLoadFailed() {
        updateCurrentZone(AdZoneData())
        notifyNoAdAvailable()
    }

    init {
        attached = false
        zoneLoaded = false
        currentAdZoneData = AdZoneData()
    }

    companion object {
        private const val PIXEL_TRACKING_JS = "loadTrackingPixels()"
    }
}