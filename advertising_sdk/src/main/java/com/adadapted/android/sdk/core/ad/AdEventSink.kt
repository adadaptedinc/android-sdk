package com.adadapted.android.sdk.core.ad

import com.adadapted.android.sdk.core.session.Session

interface AdEventSink {
    fun sendBatch(session: Session, events: Set<AdEvent>)
}