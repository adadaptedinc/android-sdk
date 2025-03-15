package com.adadapted.android.sdk.core.ad

import com.adadapted.android.sdk.core.concurrency.Transporter
import com.adadapted.android.sdk.core.concurrency.TransporterCoroutineScope
import com.adadapted.android.sdk.core.interfaces.AdAdapter
import com.adadapted.android.sdk.core.interfaces.ZoneAdListener

object AdClient  {
    private var adapter: AdAdapter? = null
    private var transporter: TransporterCoroutineScope = Transporter()
    private var hasInstance: Boolean = false

    fun fetchNewAd(zoneId: String, listener: ZoneAdListener, storeId: String = "", contextId: String = "", extra: String = "") {
        transporter.dispatchToThread { adapter?.requestAd(zoneId, listener, storeId, contextId, extra) }
    }

    fun createInstance(adapter: AdAdapter, transporter: TransporterCoroutineScope) {
        AdClient.adapter = adapter
        AdClient.transporter = transporter
        hasInstance = true
    }
}