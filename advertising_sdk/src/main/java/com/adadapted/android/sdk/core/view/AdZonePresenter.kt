package com.adadapted.android.sdk.core.view

import com.adadapted.android.sdk.constants.EventStrings
import com.adadapted.android.sdk.core.ad.Ad
import com.adadapted.android.sdk.core.ad.AdActionType
import com.adadapted.android.sdk.core.ad.AdClient
import com.adadapted.android.sdk.core.ad.AdContentPublisher
import com.adadapted.android.sdk.core.ad.AdZoneData
import com.adadapted.android.sdk.core.concurrency.Timer
import com.adadapted.android.sdk.core.concurrency.nowInSeconds
import com.adadapted.android.sdk.core.event.EventClient
import com.adadapted.android.sdk.core.event.ZoneUnfilledReasons
import com.adadapted.android.sdk.core.interfaces.ZoneAdListener
import com.adadapted.android.sdk.core.log.AALogger

interface AdZonePresenterListener {
    fun onZoneAvailable(adZoneData: AdZoneData)
    fun onAdAvailable(ad: Ad)
    fun onNoAdAvailable()
    fun onAdVisibilityChanged(ad: Ad)
}

class AdZonePresenter(
    private val adViewHandler: AdViewHandler,
    private val adClient: AdClient,
    private val now: () -> Long = ::nowInSeconds
) : ZoneAdListener {
    private var currentAd: Ad = Ad()
    private var zoneId: String = ""
    private var isZoneVisible: Boolean = true
    private var isAppInForeground: Boolean = true
    private var isInWindow: Boolean = true
    private var adZonePresenterListener: AdZonePresenterListener? = null
    private var attached: Boolean
    private var zoneMounted = false
    private var unfilledReported = false
    private var zoneContextId: String = ""
    private var zoneLoaded: Boolean
    private var currentAdZoneData: AdZoneData
    private var adFetchedAt: Long = 0
    private var secondsLeftOnRefresh: Long = 0
    private var countdownResumedAt: Long = 0
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

    fun onStart(adZonePresenterListener: AdZonePresenterListener?) {
        if (!zoneMounted) {
            zoneMounted = true
            eventClient.trackZoneMounted(zoneId) //Reported for every zone, ad or not
        }
        onAttach(adZonePresenterListener)
    }

    fun onStop() {
        onDetach()
        if (zoneMounted) {
            zoneMounted = false
            eventClient.trackZoneUnmounted(zoneId)
        }
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
                fetchAd(this) //FIRST INITIAL CALL
            }
            resumeTimer()
        }
    }

    fun onDetach() {
        if (attached) {
            attached = false
            adZonePresenterListener = null
            endImpression()
            pauseTimer()
        }
    }

    fun onAppForegrounded() {
        isAppInForeground = true
        resumeTimer()
    }

    fun onAppBackgrounded() {
        isAppInForeground = false
        endImpression()
        pauseTimer()
    }

    fun onEnteredWindow() {
        isInWindow = true
        resumeTimer()
    }

    //A zone can leave the hierarchy without ever going invisible or being stopped - a recycled row,
    //a destroyed fragment view - and it is showing an ad to no one either way
    fun onExitedWindow() {
        isInWindow = false
        endImpression()
        pauseTimer()
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

        endImpression() //Rotated out, the ad the zone was showing is done

        fetchAd(object : ZoneAdListener {
            override fun onAdLoaded(adZoneData: AdZoneData) {
                updateCurrentZone(adZoneData)
                notifyZoneAvailable()
            }

            override fun onAdLoadFailed() {
                reportZoneUnfilled(ZoneUnfilledReasons.REQUEST_FAILED)
                handleAd(clearedAdKeepingRefreshTime())
            }
        })
    }

    private fun fetchAd(listener: ZoneAdListener) {
        unfilledReported = false
        adClient.fetchNewAd(zoneId = zoneId, contextId = zoneContextId, listener = listener)
    }

    private fun reportZoneUnfilled(reason: String) {
        if (unfilledReported || !attached || !isZoneVisible) return
        unfilledReported = true
        eventClient.trackZoneUnfilled(zoneId, reason)
    }

    private fun handleAd(ad: Ad) {
        currentAd = ad
        restartTimer() //Pick up the new Ad's refresh time
        displayAd()
    }

    private fun displayAd() {
        if (currentAd.isEmpty) {
            reportZoneUnfilled(ZoneUnfilledReasons.NO_AD)
            notifyNoAdAvailable()
        } else {
            notifyAdAvailable(currentAd)
        }
    }

    fun onAdDisplayed(ad: Ad, isAdVisible: Boolean) {
        isZoneVisible = isAdVisible
        if (isAdVisible) resumeTimer() else pauseTimer()
        trackAdImpression(ad, isAdVisible)
    }

    fun onAdVisibilityChanged(isAdVisible: Boolean) {
        isZoneVisible = isAdVisible
        adZonePresenterListener?.onAdVisibilityChanged(currentAd)
        trackAdImpression(currentAd, isAdVisible)
        if (isAdVisible) {
            resumeTimer()
        } else {
            endImpression()
            pauseTimer()
        }
    }

    fun onAdDisplayFailed() {
        reportZoneUnfilled(ZoneUnfilledReasons.RENDER_FAILED)
        currentAd = clearedAdKeepingRefreshTime()
        resumeTimer()
    }

    fun onBlankDisplayed() {
        currentAd = clearedAdKeepingRefreshTime()
        resumeTimer()
    }

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

    internal fun endImpression() {
        eventClient.trackImpressionEnd(currentAd) //Only fires once, and only if a real impression was tracked
    }

    private fun callPixelTrackingJavaScript() {
        webView?.evaluateJavascript(PIXEL_TRACKING_JS) {}
        AALogger.logDebug("Pixel Tracking Called.")
    }

    //The countdown only runs while the zone is on screen in a foregrounded app
    private fun canRunTimer() = attached && isZoneVisible && isAppInForeground && isInWindow

    //Arms the countdown fresh from the current Ad's refresh time
    private fun restartTimer() {
        cancelTimer()
        adFetchedAt = now()
        secondsLeftOnRefresh = currentAd.refreshTimeOrDefault
        if (currentAd.refreshTimeWasRejected) {
            AALogger.logError("Ad refresh time of ${currentAd.refreshTime}s was served but not honored. Using ${secondsLeftOnRefresh}s")
        }
        startTimer()
    }

    //Freezes what is left of the countdown, so a zone off screen or an app in the background
    //neither refreshes nor fetches
    private fun pauseTimer() {
        if (!timerRunning) return
        secondsLeftOnRefresh = (secondsLeftOnRefresh - (now() - countdownResumedAt)).coerceAtLeast(0)
        cancelTimer()
        AALogger.logDebug("Zone timer paused with ${secondsLeftOnRefresh}s left")
    }

    //An Ad that outlived its own refresh time while the countdown was frozen is refetched instead
    //of being shown for the leftover time it never spent on screen
    private fun resumeTimer() {
        if (timerRunning || !canRunTimer()) return
        if (zoneLoaded && now() - adFetchedAt >= currentAd.refreshTimeOrDefault) {
            getNextAd()
        } else {
            startTimer()
        }
    }

    private fun startTimer() {
        if (!zoneLoaded || timerRunning || !canRunTimer()) return
        AALogger.logDebug("Zone timer starting with ${secondsLeftOnRefresh}s left of a ${currentAd.refreshTimeOrDefault}s refresh")
        timerRunning = true
        countdownResumedAt = now()
        timer = Timer({ getNextAd() }, repeatSeconds = 0, delaySeconds = secondsLeftOnRefresh)
    }

    private fun cancelTimer() {
        if (::timer.isInitialized) {
            timer.cancelTimer()
        }
        timerRunning = false
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
        reportZoneUnfilled(ZoneUnfilledReasons.REQUEST_FAILED)
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