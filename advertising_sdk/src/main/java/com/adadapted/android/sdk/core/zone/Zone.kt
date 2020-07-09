package com.adadapted.android.sdk.core.zone

import com.adadapted.android.sdk.core.ad.Ad
import com.adadapted.android.sdk.core.common.Dimension
import kotlin.collections.ArrayList

class Zone(val id: String = "",
           val dimensions: MutableMap<String, Dimension> = HashMap(),
           var ads: List<Ad> = ArrayList()) {

    fun hasAds(): Boolean {
        return ads.isNotEmpty()
    }

    fun setDimension(key: String, dimension: Dimension) {
        dimensions[key] = dimension
    }
}