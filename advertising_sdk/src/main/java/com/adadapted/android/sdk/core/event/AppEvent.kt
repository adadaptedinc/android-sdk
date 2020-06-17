package com.adadapted.android.sdk.core.event

import java.util.Date

class AppEvent internal constructor(val type: String,
                                    val name: String,
                                    val params: Map<String, String>) {
    val datetime: Long = Date().time
}