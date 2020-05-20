package com.adadapted.android.sdk.core.ad

import java.util.Date

class AdEvent
@JvmOverloads
internal constructor(val adId: String,
                     val zoneId: String,
                     val impressionId: String,
                     val eventType: String,
                     val createdAt: Long = Date().time) {

    object Types {
        const val IMPRESSION = "impression"
        const val IMPRESSION_END = "impression_end"
        const val INTERACTION = "interaction"
        const val POPUP_BEGIN = "popup_begin"
        const val POPUP_END = "popup_end"
        const val CUSTOM = "custom"
    }
}