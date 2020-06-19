package com.adadapted.android.sdk.core.session

import com.adadapted.android.sdk.core.device.DeviceInfo

interface SessionAdapter {
    interface SessionInitListener {
        fun onSessionInitialized(session: Session)
        fun onSessionInitializeFailed()
    }

    interface AdGetListener {
        fun onNewAdsLoaded(session: Session)
        fun onNewAdsLoadFailed()
    }

    interface Listener : SessionInitListener, AdGetListener

    fun sendInit(deviceInfo: DeviceInfo, listener: SessionInitListener)
    fun sendRefreshAds(session: Session, listener: AdGetListener)
}