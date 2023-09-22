package com.adadapted.android.sdk.core.view

object DimensionConverter {
    private var scale: Float = 0f

    fun createInstance(scale: Float) {
        DimensionConverter.scale = scale
    }

    fun convertDpToPx(dpValue: Int): Int {
        return if (dpValue > 0) {
            (dpValue * scale + 0.5f).toInt()
        } else dpValue
    }
}
