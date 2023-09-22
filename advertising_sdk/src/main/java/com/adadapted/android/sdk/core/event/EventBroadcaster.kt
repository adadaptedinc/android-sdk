package com.adadapted.android.sdk.core.event

import com.adadapted.android.sdk.core.concurrency.Transporter
import com.adadapted.android.sdk.core.concurrency.TransporterCoroutineScope
import com.adadapted.android.sdk.core.interfaces.EventBroadcastListener
import com.adadapted.android.sdk.core.interfaces.EventClientListener

object EventBroadcaster : EventClientListener {

    private val transporter: TransporterCoroutineScope = Transporter()
    private var listener: EventBroadcastListener? = null

    fun setListener(listener: EventBroadcastListener) {
        EventBroadcaster.listener = listener
    }

    override fun onAdEventTracked(event: AdEvent?) {
        if (listener == null || event == null) {
            return
        }
        transporter.dispatchToThread { listener?.onAdEventTracked(event.zoneId, event.eventType) }
    }

    init {
        EventClient.addListener(this)
    }
}