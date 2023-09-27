package com.adadapted.android.sdk.core.event

import com.adadapted.android.sdk.core.concurrency.Transporter
import com.adadapted.android.sdk.core.concurrency.TransporterCoroutineScope
import com.adadapted.android.sdk.core.interfaces.AaSdkEventListener
import com.adadapted.android.sdk.core.interfaces.EventClientListener

object EventBroadcaster : EventClientListener {

    private var transporter: TransporterCoroutineScope = Transporter()
    private var listener: AaSdkEventListener? = null

    fun setListener(listener: AaSdkEventListener, transporter: TransporterCoroutineScope = Transporter()) {
        EventBroadcaster.listener = listener
        EventBroadcaster.transporter = transporter
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