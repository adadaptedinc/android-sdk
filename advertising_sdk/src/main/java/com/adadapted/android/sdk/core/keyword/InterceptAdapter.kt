package com.adadapted.android.sdk.core.keyword

interface InterceptAdapter {
    interface Listener {
        fun onSuccess(intercept: Intercept)
    }

    suspend fun retrieve(sessionId: String, listener: Listener)
    suspend fun sendEvents(sessionId: String, events: MutableSet<InterceptEvent>)
}