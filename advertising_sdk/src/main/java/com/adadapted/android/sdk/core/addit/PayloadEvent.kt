package com.adadapted.android.sdk.core.addit

import java.util.Date

class PayloadEvent internal constructor(val payloadId: String, val status: String) {
    val timestamp: Long = Date().time
}