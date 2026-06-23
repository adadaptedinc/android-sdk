package com.adadapted.android.sdk.core.interfaces

interface AdAdapter {
    suspend fun requestAd(zoneId: String, listener: ZoneAdListener, storeId: String = "", contextId: String = "", extra: String = "")
}