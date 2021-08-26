package com.adadapted.android.sdk.core.ad

import java.util.Date

class AdEvent

internal constructor(val adId: String,
                     val zoneId: String,
                     val impressionId: String,
                     val eventType: String,
                     val createdAt: Long = Date().time) {

    object Types {
        const val IMPRESSION = "impression"
        const val INVISIBLE_IMPRESSION = "invisible_impression"
        const val INTERACTION = "interaction"
        const val POPUP_BEGIN = "popup_begin"
    }
}