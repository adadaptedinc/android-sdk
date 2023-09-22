package com.adadapted.android.sdk.core.interfaces

import com.adadapted.android.sdk.core.event.AdEvent

interface EventClientListener {
    fun onAdEventTracked(event: AdEvent?)
}
