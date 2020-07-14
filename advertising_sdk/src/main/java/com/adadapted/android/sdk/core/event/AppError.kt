package com.adadapted.android.sdk.core.event

import java.util.Date

class AppError internal constructor(val code: String, val message: String, val params: Map<String, String>) {
    val datetime: Long = Date().time
}