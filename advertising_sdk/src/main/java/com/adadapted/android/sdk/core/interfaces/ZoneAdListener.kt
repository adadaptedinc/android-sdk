package com.adadapted.android.sdk.core.interfaces

import com.adadapted.android.sdk.core.ad.ZoneAd

interface ZoneAdListener {
    fun onAdLoaded(zoneAd: ZoneAd)
    fun onAdLoadFailed()
}