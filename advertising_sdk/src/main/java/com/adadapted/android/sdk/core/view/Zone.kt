package com.adadapted.android.sdk.core.view

import com.adadapted.android.sdk.core.ad.Ad
import kotlinx.serialization.SerialName

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

    val dimensions by lazy {
        val dimensionsToReturn: MutableMap<String, Dimension> = HashMap()
        dimensionsToReturn[Dimension.Orientation.PORT] = Dimension(
            calculateDimensionValue(portHeight.toInt()),
            calculateDimensionValue(portWidth.toInt())
        )
        dimensionsToReturn[Dimension.Orientation.LAND] = Dimension(
            calculateDimensionValue(landHeight.toInt()),
            calculateDimensionValue(landWidth.toInt())
        )
        return@lazy dimensionsToReturn
    }

    fun hasAds(): Boolean {
        return ads.isNotEmpty()
    }

    private fun calculateDimensionValue(value: Int): Int {
        return DimensionConverter.convertDpToPx(value)
    }
}