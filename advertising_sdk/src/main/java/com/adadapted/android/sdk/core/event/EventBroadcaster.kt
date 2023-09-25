package com.adadapted.android.sdk.core.event

import com.adadapted.android.sdk.core.concurrency.Transporter
import com.adadapted.android.sdk.core.concurrency.TransporterCoroutineScope
import com.adadapted.android.sdk.core.interfaces.AaSdkEventListener
import com.adadapted.android.sdk.core.interfaces.EventClientListener

object EventBroadcaster : EventClientListener {

    private val transporter: TransporterCoroutineScope = Transporter()
    private var listener: AaSdkEventListener? = null

    fun setListener(listener: AaSdkEventListener) {
        EventBroadcaster.listener = listener
    }

    override fun onAdEventTracked(event: AdEvent?) {
        if (listener == null || event == null) {
            return
        }
        transporter.dispatchToThread { listener?.onNextAdEvent(event.zoneId, event.eventType) }
    }

    init {
        EventClient.addListener(this)
    }
}