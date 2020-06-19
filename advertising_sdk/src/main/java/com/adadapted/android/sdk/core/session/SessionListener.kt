package com.adadapted.android.sdk.core.session

abstract class SessionListener {
    open fun onPublishEvents() {}
    open fun onSessionAvailable(session: Session) {}
    open fun onAdsAvailable(session: Session) {}
    open fun onSessionExpired() {}
    open fun onSessionInitFailed() {}
}