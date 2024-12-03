package com.adadapted.android.sdk.core.view

import android.content.res.Resources
import android.util.DisplayMetrics

object DimensionConverter {
    private var scale: Float = 0f
    private val displayMetrics: DisplayMetrics = Resources.getSystem().displayMetrics
    private val screenWidthDp: Float =  displayMetrics.widthPixels / displayMetrics.density
    private val screenHeightDp: Float = displayMetrics.heightPixels / displayMetrics.density

    fun createInstance(scale: Float) {
        DimensionConverter.scale = scale
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
}
