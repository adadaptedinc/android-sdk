package com.adadapted.android.sdk.core.keyword

import com.adadapted.android.sdk.core.session.Session

interface InterceptAdapter {
    interface Listener {
        fun onSuccess(intercept: Intercept)
    }

    suspend fun retrieve(session: Session, listener: Listener)
    suspend fun sendEvents(session: Session, events: MutableSet<InterceptEvent>)
}