package com.adadapted.android.sdk.core.ad

import com.adadapted.android.sdk.core.atl.AddToListContent
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

interface AdContentListener {
    fun onContentAvailable(zoneId: String, content: AddToListContent)
    fun onNonContentAction(zoneId: String, adId: String) {}
}

private val listenerIdMap = ConcurrentHashMap<AdContentListener, String>()

val AdContentListener.listenerId: String
    get() = listenerIdMap.getOrPut(this) { UUID.randomUUID().toString() }