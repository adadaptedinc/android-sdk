package com.adadapted.android.sdk.core.interfaces

public interface AaSdkSessionListener {
    public fun onHasAdsToServe(hasAds: Boolean, availableZoneIds: List<String>)
}