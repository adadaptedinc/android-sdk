package com.adadapted.android.sdk.core.interfaces

interface AaSdkEventListener {
    fun onNextAdEvent(zoneId: String, eventType: String)
}