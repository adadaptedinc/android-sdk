package com.adadapted.android.sdk.core.event

import com.adadapted.android.sdk.core.ad.Ad

interface BaseEventClient {
    fun trackInteraction(ad: Ad)
    fun trackSdkEvent(name: String, params: Map<String, String>)
    fun trackError(code: String, message: String, params: Map<String, String> = hashMapOf())
}
