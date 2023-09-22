package com.adadapted.android.sdk.core.session

interface SessionListener {
    fun onPublishEvents() {}
    fun onSessionAvailable(session: Session) {}
    fun onAdsAvailable(session: Session) {}
    fun onSessionExpired() {}
    fun onSessionInitFailed() {}
}