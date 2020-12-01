package com.adadapted.android.sdk.core.common

class DimensionConverter(private val scale: Float) {
    fun convertDpToPx(dpValue: Int): Int {
        return if (dpValue > 0) {
            (dpValue * scale + 0.5f).toInt()
        } else dpValue
    }

    companion object {
        private val LOGTAG = DimensionConverter::class.java.name
        private lateinit var instance: DimensionConverter

        fun getInstance(): DimensionConverter {
            return instance
        }

        fun createInstance(scale: Float) {
            instance = DimensionConverter(scale)
        }
    }
}