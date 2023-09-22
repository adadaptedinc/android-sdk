package com.adadapted.android.sdk.core.interfaces

interface SessionBroadcastListener {
    fun onHasAdsToServe(hasAds: Boolean, availableZoneIds: List<String>)
}