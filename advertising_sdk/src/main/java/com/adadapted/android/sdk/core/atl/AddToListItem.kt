package com.adadapted.android.sdk.core.atl

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class AddToListItem(
    @SerialName("tracking_id")
    val trackingId: String = "",
    @SerialName("product_title")
    val title: String = "",
    @SerialName("product_brand")
    val brand: String = "",
    @SerialName("product_category")
    val category: String = "",
    @SerialName("product_barcode")
    val productUpc: String = "",
    @SerialName("product_sku")
    val retailerSku: String = "",
    //Temporarily hijacking this 'discount' parameter until a more elegant backend solutions exists in V2
    @SerialName("product_discount")
    val retailerID: String = "",
    @SerialName("product_image")
    val productImage: String = ""
)