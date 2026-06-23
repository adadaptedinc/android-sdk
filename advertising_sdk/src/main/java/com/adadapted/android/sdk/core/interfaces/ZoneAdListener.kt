package com.adadapted.android.sdk.core.interfaces

import com.adadapted.android.sdk.core.ad.AdZoneData

interface ZoneAdListener {
    fun onAdLoaded(adZoneData: AdZoneData)
    fun onAdLoadFailed()
}