package com.adadapted.android.sdk.core.zone

import com.adadapted.android.sdk.core.ad.Ad
import com.adadapted.android.sdk.core.common.Dimension
import com.adadapted.android.sdk.core.common.DimensionConverter
import com.google.gson.annotations.SerializedName
import kotlin.collections.ArrayList

class Zone(val id: String = "", var ads: List<Ad> = ArrayList()) {

    @SerializedName("port_height")
    var portHeight: Long = 0

    @SerializedName("port_width")
    var portWidth: Long = 0

    @SerializedName("land_height")
    var landHeight: Long = 0

    @SerializedName("land_width")
    var landWidth: Long = 0

    val dimensions by lazy {
     val dimensionsToReturn: MutableMap<String, Dimension> = HashMap()
        dimensionsToReturn[Dimension.Orientation.PORT] = Dimension(calculateDimensionValue(portHeight.toInt()), calculateDimensionValue(portWidth.toInt()))
        dimensionsToReturn[Dimension.Orientation.LAND] = Dimension(calculateDimensionValue(landHeight.toInt()), calculateDimensionValue(landWidth.toInt()))
        return@lazy dimensionsToReturn
    }

    fun hasAds(): Boolean {
        return ads.isNotEmpty()
    }

    private fun calculateDimensionValue(value: Int): Int {
        return DimensionConverter.getInstance().convertDpToPx(value)
    }
}