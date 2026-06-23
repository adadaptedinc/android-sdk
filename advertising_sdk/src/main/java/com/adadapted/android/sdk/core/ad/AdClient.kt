package com.adadapted.android.sdk.core.ad

import com.adadapted.android.sdk.core.concurrency.Transporter
import com.adadapted.android.sdk.core.concurrency.TransporterCoroutineScope
import com.adadapted.android.sdk.core.interfaces.AdAdapter
import com.adadapted.android.sdk.core.interfaces.ZoneAdListener

import java.util.concurrent.ConcurrentLinkedQueue

object AdClient {
    private var adapter: AdAdapter? = null
    private var transporter: TransporterCoroutineScope = Transporter()
    private val pendingRequests = ConcurrentLinkedQueue<() -> Unit>()

    fun fetchNewAd(
        zoneId: String,
        listener: ZoneAdListener,
        storeId: String = "",
        contextId: String = "",
        extra: String = ""
    ) {
        if (adapter == null) {
            // Store the request to be retried later
            pendingRequests.add { fetchNewAd(zoneId, listener, storeId, contextId, extra) }
            return
        }

        transporter.dispatchToThread {
            adapter?.requestAd(zoneId, listener, storeId, contextId, extra)
        }
    }

    fun createInstance(adapter: AdAdapter, transporter: TransporterCoroutineScope) {
        AdClient.adapter = adapter
        AdClient.transporter = transporter

        // Process any pending requests now that adapter is available
        while (pendingRequests.isNotEmpty()) {
            pendingRequests.poll()?.invoke()
        }
    }
}
