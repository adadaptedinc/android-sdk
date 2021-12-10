package com.adadapted.android.sdk.ui.view

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.adadapted.android.sdk.config.EventStrings
import com.adadapted.android.sdk.core.ad.Ad
import com.adadapted.android.sdk.core.ad.AdActionType
import com.adadapted.android.sdk.core.ad.AdEventClient
import com.adadapted.android.sdk.core.event.AppEventClient
import com.adadapted.android.sdk.core.session.Session
import com.adadapted.android.sdk.core.session.SessionClient
import com.adadapted.android.sdk.core.session.SessionListener
import com.adadapted.android.sdk.core.zone.Zone
import com.adadapted.android.sdk.ui.activity.AaWebViewPopupActivity
import com.adadapted.android.sdk.ui.messaging.AdContentPublisher
import java.util.Timer
import java.util.TimerTask
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock

internal class AdZonePresenter(private val context: Context, private val pixelWebView: PixelWebView = PixelWebView(context.applicationContext), private val aaWebViewPopupActivity: AaWebViewPopupActivity = AaWebViewPopupActivity())
    : SessionListener() {
    internal interface Listener {
        fun onZoneAvailable(zone: Zone)
        fun onAdsRefreshed(zone: Zone)
        fun onAdAvailable(ad: Ad)
        fun onNoAdAvailable()
    }

    private var currentAd: Ad = Ad()
    private var zoneId: String? = null
    private var listener: Listener? = null
    private var attached: Boolean
    private val zoneLock: Lock = ReentrantLock()
    private var sessionId: String? = null
    private var zoneLoaded: Boolean
    private var currentZone: Zone
    private var randomAdStartPosition: Int
    private var adStarted = false
    private var adCompleted = false
    private var timerRunning = false
    private val timerLock: Lock = ReentrantLock()
    private var timer: Timer
    private val adEventClient: AdEventClient = AdEventClient.getInstance()
    private val appEventClient: AppEventClient = AppEventClient.getInstance()
    private val sessionClient: SessionClient = SessionClient.getInstance()

    fun init(zoneId: String) {
        if (this.zoneId == null) {
            this.zoneId = zoneId
            val params: MutableMap<String, String> = HashMap()
            params["zone_id"] = zoneId
            appEventClient.trackSdkEvent(EventStrings.ZONE_LOADED, params)
        }
    }

    fun onAttach(l: Listener?) {
        if (l == null) {
            Log.e(LOGTAG, "NULL Listener provided")
            return
        }
        zoneLock.lock()
        try {
            if (!attached) {
                attached = true
                listener = l
                sessionClient.addPresenter(this)
            }
            setNextAd()
        } finally {
            zoneLock.unlock()
        }
    }

    fun onDetach() {
        zoneLock.lock()
        try {
            if (attached) {
                attached = false
                listener = null
                completeCurrentAd()
                sessionClient.removePresenter(this)
            }
        } finally {
            zoneLock.unlock()
        }
    }

    private fun setNextAd() {
        if (!zoneLoaded || timerRunning) {
            return
        }
        completeCurrentAd()
        zoneLock.lock()
        try {
            currentAd = if (listener != null && currentZone.hasAds()) {
                val adPosition = randomAdStartPosition % currentZone.ads.size
                randomAdStartPosition++
                currentZone.ads[adPosition]
            } else {
                Ad()
            }
            adStarted = false
            adCompleted = false
        } finally {
            zoneLock.unlock()
        }
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
            zoneLock.lock()
            try {
                if (!currentAd.impressionWasTracked()) {
                    adEventClient.trackInvisibleImpression(currentAd)
                }
                currentAd.resetImpressionTracking() //this is critical to make sure rotating ads can get more than one impression (total)
                adCompleted = true
            } finally {
                zoneLock.unlock()
            }
        }
    }

    fun onAdDisplayed(ad: Ad, isAdVisible: Boolean) {
        zoneLock.lock()
        try {
            adStarted = true
            trackAdImpression(ad, isAdVisible)
            startZoneTimer()
        } finally {
            zoneLock.unlock()
        }
    }

    fun onAdVisibilityChanged(isAdVisible: Boolean) {
        trackAdImpression(currentAd, isAdVisible)
    }

    fun onAdDisplayFailed() {
        zoneLock.lock()
        try {
            adStarted = true
            currentAd = Ad()
            startZoneTimer()
        } finally {
            zoneLock.unlock()
        }
    }

    fun onBlankDisplayed() {
        zoneLock.lock()
        try {
            adStarted = true
            currentAd = Ad()
            startZoneTimer()
        } finally {
            zoneLock.unlock()
        }
    }

    private fun trackAdImpression(ad: Ad, isAdVisible: Boolean) {
        if (!isAdVisible || ad.impressionWasTracked() || ad.isEmpty) return
        ad.setImpressionTracked()
        adEventClient.trackImpression(ad)
        pixelWebView.loadData(ad.trackingHtml, "text/html", null)
    }

    private fun startZoneTimer() {
        if (!zoneLoaded || timerRunning) {
            return
        }
        timerLock.lock()
        try {
            timerRunning = true
            timer.schedule(object : TimerTask() {
                override fun run() {
                    timerLock.lock()
                    timerRunning = try {
                        false
                    } finally {
                        timerLock.unlock()
                    }
                    setNextAd()
                }
            }, currentAd.refreshTime * 1000)
        } finally {
            timerLock.unlock()
        }
    }

    fun onAdClicked(ad: Ad) {
        val actionType = ad.actionType
        val params: MutableMap<String, String> = HashMap()
        params["ad_id"] = ad.id

        when (actionType) {
            AdActionType.CONTENT -> {
                appEventClient.trackSdkEvent(EventStrings.ATL_AD_CLICKED, params)
                handleContentAction(ad)
            }
            AdActionType.LINK, AdActionType.EXTERNAL_LINK -> {
                adEventClient.trackInteraction(ad)
                handleLinkAction(ad)
            }
            AdActionType.POPUP -> {
                adEventClient.trackInteraction(ad)
                handlePopupAction(ad)
            }
            AdActionType.CONTENT_POPUP -> {
                appEventClient.trackSdkEvent(EventStrings.POPUP_AD_CLICKED, params)
                handlePopupAction(ad)
            }
            else -> Log.w(LOGTAG, "Cannot handle Action type: $actionType")
        }

        cycleToNextAdIfPossible()
    }

    private fun cycleToNextAdIfPossible() {
        if (currentZone.ads.count() > 1) {
            restartTimer()
            setNextAd()
        }
    }

    private fun restartTimer() {
        timer.cancel()
        timerRunning = false
        timer = Timer()
    }

    private fun handleContentAction(ad: Ad) {
        val zoneId = ad.zoneId
        AdContentPublisher.getInstance().publishContent(zoneId, ad.getContent())
    }

    private fun handleLinkAction(ad: Ad) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.data = Uri.parse(ad.actionPath)
        context.startActivity(intent)
    }

    private fun handlePopupAction(ad: Ad) {
        val intent = aaWebViewPopupActivity.createActivity(context, ad)
        context.startActivity(intent)
    }

    private fun notifyZoneAvailable() {
        listener?.onZoneAvailable(currentZone)
    }

    private fun notifyAdsRefreshed() {
        listener?.onAdsRefreshed(currentZone)
    }

    private fun notifyAdAvailable(ad: Ad) {
        listener?.onAdAvailable(ad)
    }

    private fun notifyNoAdAvailable() {
        listener?.onNoAdAvailable()
    }

    private fun updateSessionId(sessionId: String): Boolean {
        zoneLock.lock()
        try {
            if (this.sessionId == null || this.sessionId != sessionId) {
                this.sessionId = sessionId
                return true
            }
        } finally {
            zoneLock.unlock()
        }
        return false
    }

    private fun updateCurrentZone(zone: Zone) {
        zoneLock.lock()
        try {
            zoneLoaded = true
            currentZone = zone
        } finally {
            zoneLock.unlock()
        }
        if (currentAd.isEmpty) {
            setNextAd()
        }
    }

    override fun onSessionAvailable(session: Session) {
        updateCurrentZone(session.getZone(zoneId!!))
        if (updateSessionId(session.id)) {
            notifyZoneAvailable()
        }
    }

    override fun onAdsAvailable(session: Session) {
        updateCurrentZone(session.getZone(zoneId!!))
        notifyAdsRefreshed()
    }

    override fun onSessionInitFailed() {
        updateCurrentZone(Zone())
    }

    companion object {
        private val LOGTAG = AdZonePresenter::class.java.name
    }

    init {
        attached = false
        zoneLoaded = false
        currentZone = Zone()
        randomAdStartPosition = (Math.random() * 10).toInt()
        timer = Timer()
    }
}