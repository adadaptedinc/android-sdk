package com.adadapted.android.sdk.core.interfaces

import com.adadapted.android.sdk.core.ad.ZoneAd

interface AdZoneListener {
    fun onAdLoaded(zoneAd: ZoneAd)
    fun onAdLoadFailed()
}