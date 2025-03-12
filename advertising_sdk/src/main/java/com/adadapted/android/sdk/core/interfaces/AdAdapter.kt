package com.adadapted.android.sdk.core.interfaces

interface AdAdapter {
    suspend fun requestAd(listener: ZoneAdListener)
}