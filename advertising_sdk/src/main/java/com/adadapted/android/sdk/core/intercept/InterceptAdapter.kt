package com.adadapted.android.sdk.core.intercept

import com.adadapted.android.sdk.core.session.Session

interface InterceptAdapter {
    fun retrieve(session: Session?, callback: Callback?)
    fun sendEvents(session: Session?, events: MutableSet<InterceptEvent>?)
    interface Callback {
        fun onSuccess(intercept: Intercept)
    }
}