package com.adadapted.android.sdk.core.view

import com.adadapted.android.sdk.core.ad.Ad
import kotlinx.serialization.SerialName
import kotlinx.serialization.Transient

@kotlinx.serialization.Serializable
data class Zone(val id: String = "", val ads: List<Ad> = listOf()) {

    @SerialName("port_height")
    val portHeight: Long = 0

    @SerialName("port_width")
    val portWidth: Long = 0

    @SerialName("land_height")
    val landHeight: Long = 0

    @SerialName("land_width")
    val landWidth: Long = 0

    @Transient
    val dimensions: Map<String, Dimension> = initializeDimensions()

    @Transient
    val pixelAccurateDimensions: Map<String, Dimension> = initializePixelAccurateDimensions()

    fun hasAds(): Boolean {
        return ads.isNotEmpty()
    }

    private fun initializeDimensions(): Map<String, Dimension> {
        return mapOf(
            Dimension.Orientation.PORT to Dimension(
                calculateDimensionValue(portHeight.toInt()),
                calculateDimensionValue(portWidth.toInt())
            ),
            Dimension.Orientation.LAND to Dimension(
                calculateDimensionValue(landHeight.toInt()),
                calculateDimensionValue(landWidth.toInt())
            )
        )
    }

    private fun initializePixelAccurateDimensions(): Map<String, Dimension> {
        return mapOf(
            Dimension.Orientation.PORT to calculatePixelAccurateDimensionValue(portWidth.toInt(), portHeight.toInt()),
            Dimension.Orientation.LAND to calculatePixelAccurateDimensionValue(landWidth.toInt(), landHeight.toInt())
        )
    }

    private fun calculateDimensionValue(value: Int): Int {
        return DimensionConverter.convertDpToPx(value)
    }

    private fun calculatePixelAccurateDimensionValue(width: Int, height: Int): Dimension {
        return DimensionConverter.scaleDimensions(width, height)
    }
}