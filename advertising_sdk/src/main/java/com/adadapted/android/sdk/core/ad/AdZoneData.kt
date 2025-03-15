package com.adadapted.android.sdk.core.ad

import com.adadapted.android.sdk.core.view.Dimension
import com.adadapted.android.sdk.core.view.DimensionConverter
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class AdZoneData(
    val ad: Ad = Ad(),
    val type: String = "",
    @SerialName("port_height") val portHeight: Int = 0,
    @SerialName("port_width") val portWidth: Int = 0,
) {
    @Transient
    val dimensions: Dimension = initializeDimensions()

    @Transient
    var pixelAccurateDimensions: Dimension = calculatePixelAccurateDimensions()

    fun hasAd(): Boolean {
        return ad.id.isNotEmpty()
    }

    fun rescaleDimensionsForTablet() {
        DimensionConverter.refreshDisplayMetrics()
        pixelAccurateDimensions = calculatePixelAccurateDimensions()
    }

    private fun initializeDimensions(): Dimension {
        return Dimension(
            calculateDimensionValue(portHeight.toInt()),
            calculateDimensionValue(portWidth.toInt())
        )
    }

    private fun calculateDimensionValue(value: Int): Int {
        return DimensionConverter.convertDpToPx(value)
    }

    private fun calculatePixelAccurateDimensions(): Dimension {
        return calculatePixelAccurateDimensionValue(portWidth.toInt(), portHeight.toInt())
    }

    private fun calculatePixelAccurateDimensionValue(width: Int, height: Int): Dimension {
        return DimensionConverter.scaleDimensions(width, height)
    }
}