package com.adadapted.android.sdk.core.view

import android.util.DisplayMetrics
import kotlin.math.roundToInt

object DimensionConverter {
    private var scale: Float = 0f
    private var displayMetrics: DisplayMetrics = DisplayMetrics()
    private var screenWidthDp: Float = 0f
    private var screenHeightDp: Float = 0f

    fun createInstance(scale: Float, displayMetrics: DisplayMetrics) {
        DimensionConverter.scale = scale
        DimensionConverter.displayMetrics = displayMetrics
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

    fun adjustDimensionsForPadding(
        originalWidth: Int,
        originalHeight: Int,
        zonePadding: ZonePadding
    ): Dimension {
        val paddingThreshold = 0
        val coefficient = 0.85f
        var totalHorizontalPaddingPx = (zonePadding.start + zonePadding.end) * scale
        var totalVerticalPaddingPx = (zonePadding.top + zonePadding.bottom) * scale

        if (totalHorizontalPaddingPx > paddingThreshold) totalHorizontalPaddingPx *= coefficient
        if (totalVerticalPaddingPx > paddingThreshold) totalVerticalPaddingPx *= coefficient

        val adjustedWidth = (originalWidth - totalHorizontalPaddingPx).coerceAtLeast(0f)
        val adjustedHeight = (originalHeight - totalVerticalPaddingPx).coerceAtLeast(0f)
        val scaledDimensions = scaleDimensions(adjustedWidth.roundToInt(), adjustedHeight.roundToInt())
        return scaledDimensions
    }
}
