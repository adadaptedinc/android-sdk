package com.adadapted.android.sdk.core.interfaces

import com.adadapted.android.sdk.core.session.Session

interface SessionInitListener {
    fun onSessionInitialized(session: Session)
    fun onSessionInitializeFailed()
}