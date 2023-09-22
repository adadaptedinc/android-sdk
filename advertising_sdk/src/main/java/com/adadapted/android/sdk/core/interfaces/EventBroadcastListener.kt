package com.adadapted.android.sdk.core.interfaces

interface EventBroadcastListener {
    fun onAdEventTracked(zoneId: String, eventType: String)
}