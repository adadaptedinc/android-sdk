package com.adadapted.android.sdk.core.view

import com.adadapted.android.sdk.constants.Config
import com.adadapted.android.sdk.constants.EventStrings
import com.adadapted.android.sdk.core.ad.Ad
import com.adadapted.android.sdk.core.ad.AdActionType
import com.adadapted.android.sdk.core.ad.AdClient
import com.adadapted.android.sdk.core.ad.AdContentPublisher
import com.adadapted.android.sdk.core.ad.AdZoneData
import com.adadapted.android.sdk.core.concurrency.Timer
import com.adadapted.android.sdk.core.concurrency.monotonicSeconds
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
    private val now: () -> Long = ::monotonicSeconds //The countdown's own clock, not the wall clock
) : ZoneAdListener {
    @Volatile private var currentAd: Ad = Ad()
    private var zoneId: String = ""
    @Volatile private var isZoneVisible: Boolean = true
    @Volatile private var isAppInForeground: Boolean = true
    @Volatile private var isInWindow: Boolean = true
    private var adZonePresenterListener: AdZonePresenterListener? = null
    @Volatile private var attached: Boolean = false
    @Volatile private var zoneMounted = false
    @Volatile private var unfilledReported = false
    private var zoneContextId: String = ""
    @Volatile private var zoneLoaded: Boolean = false
    @Volatile private var isCurrentAdRendered = false
    private var currentAdZoneData: AdZoneData = AdZoneData()
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
        if (!zoneMounted && zoneId.isEmpty()) {
            AALogger.logError("AdZoneId is empty. Was onStart() called before init()?")
        }
        mountZone()
        onAttach(adZonePresenterListener)
    }

    //The host's onStart is the mount, but the view also attaches itself whenever it is shown, so a
    //zone shown again after onStop (or never started at all) is mounted here instead of serving unmounted
    private fun mountZone() {
        if (zoneMounted || zoneId.isEmpty()) return
        zoneMounted = true
        eventClient.trackZoneMounted(zoneId)
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
            mountZone()
            //First attach only. After that resumeTimer refetches once the refresh time, or a no-fill's backoff, is up
            if (!zoneLoaded) {
                fetchAd(this) //FIRST INITIAL CALL
            }
            resumeIfOnScreen()
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
        resumeIfOnScreen()
    }

    fun onAppBackgrounded() {
        isAppInForeground = false
        endImpression(publishNow = true) //The process can be frozen before the next publish tick
        pauseTimer()
    }

    fun onEnteredWindow() {
        isInWindow = true
        resumeIfOnScreen()
    }

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
                handleAd(clearedAdBackingOffAfterFailedRequest())
            }
        })
    }

    private fun fetchAd(listener: ZoneAdListener) {
        unfilledReported = false
        adClient.fetchNewAd(zoneId = zoneId, contextId = zoneContextId, listener = listener)
    }

    @Synchronized
    private fun reportZoneUnfilled(reason: String) {
        if (unfilledReported || !zoneIsOnScreen()) return
        unfilledReported = true
        eventClient.trackZoneUnfilled(zoneId, reason)
    }

    private fun handleAd(ad: Ad) {
        currentAd = ad
        isCurrentAdRendered = false
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
        if (ad.id != currentAd.id) {
            currentAd = ad
        }
        isCurrentAdRendered = true
        if (isAdVisible) resumeTimer() else pauseTimer()
        trackAdImpression()
    }

    fun onAdVisibilityChanged(isAdVisible: Boolean) {
        isZoneVisible = isAdVisible
        adZonePresenterListener?.onAdVisibilityChanged(currentAd)
        if (isAdVisible) {
            resumeIfOnScreen()
        } else {
            endImpression()
            pauseTimer()
        }
    }

    fun onAdDisplayFailed() {
        reportZoneUnfilled(ZoneUnfilledReasons.RENDER_FAILED)
        clearCurrentAd()
    }

    fun onBlankDisplayed() {
        clearCurrentAd()
    }

    private fun clearCurrentAd() {
        endImpression()
        currentAd = clearedAdKeepingRefreshTime()
        isCurrentAdRendered = false
        resumeTimer()
    }

    private fun clearedAdKeepingRefreshTime() = Ad(refreshTime = currentAd.refreshTime)

    //A failing server keeps a longer served backoff, but is never retried faster than the default
    private fun clearedAdBackingOffAfterFailedRequest() =
        Ad(refreshTime = maxOf(currentAd.refreshTimeOrDefault, Config.DEFAULT_AD_REFRESH_SECONDS))

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

    private fun trackAdImpression() {
        if (!zoneIsOnScreen() || !isCurrentAdRendered || currentAd.impressionWasTracked() || currentAd.isEmpty) return
        callPixelTrackingJavaScript()
        eventClient.trackImpression(currentAd)
    }

    //Also catches up on the impression the zone missed while it was off screen
    private fun resumeIfOnScreen() {
        trackAdImpression()
        resumeTimer()
    }

    //Only fires once, and only if a real impression was tracked
    internal fun endImpression(publishNow: Boolean = false) {
        eventClient.trackImpressionEnd(currentAd)
        if (publishNow) {
            eventClient.onPublishEvents()
        }
    }

    private fun callPixelTrackingJavaScript() {
        webView?.evaluateJavascript(PIXEL_TRACKING_JS) {}
        AALogger.logDebug("Pixel Tracking Called.")
    }

    //A zone is only in front of someone while it is attached and visible, in the window, and the app
    //is in the foreground. The countdown and the no-fill report both hang off that
    private fun zoneIsOnScreen() = attached && isZoneVisible && isAppInForeground && isInWindow

    //Arms the countdown fresh from the current Ad's refresh time
    @Synchronized
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
    @Synchronized
    private fun pauseTimer() {
        if (!timerRunning) return
        secondsLeftOnRefresh = (secondsLeftOnRefresh - (now() - countdownResumedAt)).coerceAtLeast(0)
        cancelTimer()
        AALogger.logDebug("Zone timer paused with ${secondsLeftOnRefresh}s left")
    }

    //An Ad that outlived its own refresh time while the countdown was frozen is refetched instead
    //of being shown for the leftover time it never spent on screen
    @Synchronized
    private fun resumeTimer() {
        if (timerRunning || !zoneIsOnScreen()) return
        if (zoneLoaded && now() - adFetchedAt >= currentAd.refreshTimeOrDefault) {
            getNextAd()
        } else {
            startTimer()
        }
    }

    private fun startTimer() {
        if (!zoneLoaded || timerRunning || !zoneIsOnScreen()) return
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
    }

    companion object {
        private const val PIXEL_TRACKING_JS = "loadTrackingPixels()"
    }
}