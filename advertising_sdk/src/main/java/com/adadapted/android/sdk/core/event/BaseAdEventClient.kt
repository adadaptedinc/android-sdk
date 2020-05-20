package com.adadapted.android.sdk.core.event

import com.adadapted.android.sdk.core.ad.Ad
import com.adadapted.android.sdk.core.ad.AdEventClient

interface BaseAdEventClient {
    fun trackInteraction(ad: Ad)
    fun addListener(listener: AdEventClient.Listener)
    fun removeListener(listener: AdEventClient.Listener)
    fun trackImpression(ad: Ad)
    fun trackImpressionEnd(ad: Ad)
    fun trackPopupBegin(ad: Ad)
    fun trackPopupEnd(ad: Ad)
    fun publishEvents()
}