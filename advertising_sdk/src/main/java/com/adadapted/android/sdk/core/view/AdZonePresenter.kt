package com.adadapted.android.sdk.core.view

import com.adadapted.android.sdk.constants.EventStrings
import com.adadapted.android.sdk.core.ad.Ad
import com.adadapted.android.sdk.core.ad.AdActionType
import com.adadapted.android.sdk.core.ad.AdContentPublisher
import com.adadapted.android.sdk.core.concurrency.Timer
import com.adadapted.android.sdk.core.event.EventClient
import com.adadapted.android.sdk.core.log.AALogger
import com.adadapted.android.sdk.core.session.Session
import com.adadapted.android.sdk.core.session.SessionClient
import com.adadapted.android.sdk.core.session.SessionListener
import java.util.Date

interface AdZonePresenterListener {
    fun onZoneAvailable(zone: Zone)
    fun onAdsRefreshed(zone: Zone)
    fun onAdAvailable(ad: Ad)
    fun onNoAdAvailable()
    fun onAdVisibilityChanged(ad: Ad)
}

class AdZonePresenter(private val adViewHandler: AdViewHandler, private val sessionClient: SessionClient?) : SessionListener {
    private var currentAd: Ad = Ad()
    private var zoneId: String = ""
    private var isZoneVisible: Boolean = true
    private var adZonePresenterListener: AdZonePresenterListener? = null
    private var attached: Boolean
    private var sessionId: String? = null
    private var zoneLoaded: Boolean
    private var currentZone: Zone
    private var randomAdStartPosition: Int
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
            sessionClient?.addPresenter(this)
        }
    }

    fun onDetach() {
        if (attached) {
            attached = false
            adZonePresenterListener = null
            completeCurrentAd()
            sessionClient?.removePresenter(this)
            stopTimer()
        }
    }

    fun setZoneContext(contextId: String) {
        sessionClient?.setZoneContext(ZoneContext(this.zoneId, contextId))
        eventClient.trackRecipeContextEvent(contextId, this.zoneId)
    }

    fun removeZoneContext() {
        sessionClient?.removeZoneContext(this.zoneId)
    }

    fun clearZoneContext() {
        sessionClient?.clearZoneContext()
    }

    private fun setNextAd(isFreshLoad: Boolean = false) {
        if (!zoneLoaded || sessionClient?.hasStaleAds() == true) {
            return
        }
        completeCurrentAd()

        currentAd = if (adZonePresenterListener != null && currentZone.hasAds()) {
            if (!isFreshLoad) {
                randomAdStartPosition++
            }
            val adPosition = randomAdStartPosition % currentZone.ads.size
            currentZone.ads[adPosition]
        } else {
            Ad()
        }

        adStarted = false
        adCompleted = false
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
            if (!currentAd.impressionWasTracked() && !isZoneVisible) {
                eventClient.trackInvisibleImpression(currentAd)
            }
            currentAd.resetImpressionTracking() //this is critical to make sure rotating ads can get more than one impression total
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
        startZoneTimer()
        adStarted = true
        currentAd = Ad()
    }

    fun onBlankDisplayed() {
        startZoneTimer()
        adStarted = true
        currentAd = Ad()
    }

    fun onAdClicked(ad: Ad) {
        val actionType = ad.actionType
        val params: MutableMap<String, String> = HashMap()
        params["ad_id"] = ad.id

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

        cycleToNextAdIfPossible()
    }

    fun onReportAdClicked(adId: String, udid: String) {
        adViewHandler.handleReportAd(adId, udid)
    }

    private fun trackAdImpression(ad: Ad, isAdVisible: Boolean) {
        if (!isAdVisible || ad.impressionWasTracked() || ad.isEmpty || webView?.loaded == false) return
        ad.setImpressionTracked()
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
        val timerDelay = currentAd.refreshTime * 1000
        timerRunning = true
        timer = Timer({
            setNextAd()
        }, timerDelay, timerDelay)
    }

    private fun cycleToNextAdIfPossible() {
        if (currentZone.ads.count() > 1) {
            restartTimer()
            setNextAd()
        }
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
        adZonePresenterListener?.onZoneAvailable(currentZone)
    }

    private fun notifyAdsRefreshed() {
        adZonePresenterListener?.onAdsRefreshed(currentZone)
    }

    private fun notifyAdAvailable(ad: Ad) {
        adZonePresenterListener?.onAdAvailable(ad)
    }

    private fun notifyNoAdAvailable() {
        AALogger.logInfo("No ad available")
        adZonePresenterListener?.onNoAdAvailable()
    }

    private fun updateSessionId(sessionId: String): Boolean {
        if (this.sessionId == null || this.sessionId != sessionId) {
            this.sessionId = sessionId
            return true
        }
        return false
    }

    private fun updateCurrentZone(zone: Zone) {
        zoneLoaded = true
        currentZone = zone
        restartTimer()
        setNextAd(isFreshLoad = true)
    }

    override fun onSessionAvailable(session: Session) {
        if (zoneId.isEmpty()) {
            AALogger.logError("AdZoneId is empty. Was onStop() called outside the host view's overriding function?")
        }
        updateCurrentZone(session.getZone(zoneId))
        if (updateSessionId(session.id)) {
            notifyZoneAvailable()
        }
    }

    override fun onAdsAvailable(session: Session) {
        updateCurrentZone(session.getZone(zoneId))
        notifyAdsRefreshed()
    }

    override fun onSessionInitFailed() {
        updateCurrentZone(Zone())
    }

    init {
        attached = false
        zoneLoaded = false
        currentZone = Zone()
        randomAdStartPosition = ((Date().time / 1000).toInt() % 10)
    }

    companion object {
        private const val PIXEL_TRACKING_JS = "loadTrackingPixels()"
    }
}