package com.adadapted.android.sdk.ui.messaging

interface AaSdkEventListener {
    fun onNextAdEvent(zoneId: String, eventType: String)
}