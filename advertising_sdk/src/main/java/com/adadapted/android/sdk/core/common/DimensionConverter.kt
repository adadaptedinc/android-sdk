package com.adadapted.android.sdk.core.common

class DimensionConverter(private val scale: Float) {
    fun convertDpToPx(dpValue: Int): Int {
        return if (dpValue > 0) {
            (dpValue * scale + 0.5f).toInt()
        } else dpValue
    }
}