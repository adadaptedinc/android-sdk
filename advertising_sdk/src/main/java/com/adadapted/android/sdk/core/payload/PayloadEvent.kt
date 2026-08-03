package com.adadapted.android.sdk.core.payload

import com.adadapted.android.sdk.core.concurrency.nowInSeconds

class PayloadEvent internal constructor(val payloadId: String, val status: String) {
    val timestamp: Long = nowInSeconds()
}