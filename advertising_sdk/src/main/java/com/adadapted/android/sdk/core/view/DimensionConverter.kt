package com.adadapted.android.sdk.core.view

import android.util.DisplayMetrics

object DimensionConverter {
    private var scale: Float = 0f
    private var displayMetrics: DisplayMetrics = DisplayMetrics()
    private var screenWidthDp: Float = 0f
    private var screenHeightDp: Float = 0f
    private var paddingConversion: Float = 0f

    fun createInstance(scale: Float, displayMetrics: DisplayMetrics) {
        DimensionConverter.scale = scale
        DimensionConverter.displayMetrics = displayMetrics
        paddingConversion = scale + 0.2f
        screenWidthDp =  displayMetrics.widthPixels / displayMetrics.density
        screenHeightDp = displayMetrics.heightPixels / displayMetrics.density
    }

    fun convertDpToPx(dpValue: Int): Int {
        return if (dpValue > 0) {
            (dpValue * scale + 0.5f).toInt()
        } else dpValue
    }

    fun scaleDimensions(originalWidth: Int, originalHeight: Int): Dimension {
        val aspectRatio = originalWidth.toFloat() / originalHeight.toFloat()

        return if (screenWidthDp / aspectRatio <= screenHeightDp) {
            val scaledWidth = (screenWidthDp * displayMetrics.density).toInt()
            Dimension(
                width = scaledWidth,
                height = (scaledWidth / aspectRatio).toInt()
            )
        } else {
            val scaledHeight = (screenHeightDp * displayMetrics.density).toInt()
            Dimension(
                width = (scaledHeight * aspectRatio).toInt(),
                height = scaledHeight
            )
        }
    }

    fun adjustDimensionsForPadding(convertedWidth: Int, convertedHeight: Int, padding: Int): Dimension {
        val adjustedWidth = (convertedWidth - paddingConversion * padding).coerceAtLeast(0f).toInt()
        val adjustedHeight = (convertedHeight - paddingConversion * padding).coerceAtLeast(0f).toInt()

        return Dimension(
            width = adjustedWidth,
            height = adjustedHeight
        )
    }
}
