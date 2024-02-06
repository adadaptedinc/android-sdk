package com.adadapted.android.sdk.core.interfaces

import com.adadapted.android.sdk.core.session.Session

interface AdGetListener {
    fun onNewAdsLoaded(session: Session)
    fun onNewAdsLoadFailed()
}