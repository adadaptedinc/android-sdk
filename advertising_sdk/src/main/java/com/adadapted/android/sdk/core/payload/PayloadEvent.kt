package com.adadapted.android.sdk.core.payload

import java.util.Date

class PayloadEvent internal constructor(val payloadId: String, val status: String) {
    val timestamp: Long = Date().time / 1000
}