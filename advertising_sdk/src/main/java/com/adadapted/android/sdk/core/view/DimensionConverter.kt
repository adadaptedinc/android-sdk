package com.adadapted.android.sdk.core.view

import android.content.res.Resources
import android.util.DisplayMetrics

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

    fun scaleDimensions(originalWidth: Int, originalHeight: Int): Dimension {
        val displayMetrics: DisplayMetrics = Resources.getSystem().displayMetrics
        val screenWidthDp = displayMetrics.widthPixels / displayMetrics.density
        val screenHeightDp = displayMetrics.heightPixels / displayMetrics.density

        // Calculate the aspect ratio of the original dimensions
        val aspectRatio = originalWidth.toFloat() / originalHeight.toFloat()

        // Scale dimensions proportionally to fit the device
        val scaledWidth: Int
        val scaledHeight: Int
        if (screenWidthDp / aspectRatio <= screenHeightDp) {
            scaledWidth = (screenWidthDp * displayMetrics.density).toInt()
            scaledHeight = (scaledWidth / aspectRatio).toInt()
        } else {
            scaledHeight = (screenHeightDp * displayMetrics.density).toInt()
            scaledWidth = (scaledHeight * aspectRatio).toInt()
        }

        return Dimension(width = scaledWidth, height = scaledHeight)
    }
}
