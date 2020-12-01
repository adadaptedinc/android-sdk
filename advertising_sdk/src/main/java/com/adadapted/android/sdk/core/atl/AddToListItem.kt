package com.adadapted.android.sdk.core.atl

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class AddToListItem : Parcelable {
    val trackingId: String
    @SerializedName("product_title")
    val title: String
    @SerializedName("product_brand")
    val brand: String
    @SerializedName("product_category")
    val category: String
    @SerializedName("product_barcode")
    val productUpc: String
    @SerializedName("product_sku")
    val retailerSku: String
    @SerializedName("product_discount")
    val discount: String
    @SerializedName("product_image")
    val productImage: String

    constructor(
            trackingId: String,
            title: String,
            brand: String,
            category: String,
            productUpc: String,
            retailerSku: String,
            discount: String,
            productImage: String
    ) {
        this.trackingId = trackingId
        this.title = title
        this.brand = brand
        this.category = category
        this.productUpc = productUpc
        this.retailerSku = retailerSku
        this.discount = discount
        this.productImage = productImage
    }

    private constructor(parcel: Parcel) {
        trackingId = parcel.readString()
        title = parcel.readString()
        brand = parcel.readString()
        category = parcel.readString()
        productUpc = parcel.readString()
        retailerSku = parcel.readString()
        discount = parcel.readString()
        productImage = parcel.readString()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(trackingId)
        dest.writeString(title)
        dest.writeString(brand)
        dest.writeString(category)
        dest.writeString(productUpc)
        dest.writeString(retailerSku)
        dest.writeString(discount)
        dest.writeString(productImage)
    }

    override fun toString(): String {
        return "AddToListItem{" +
                "trackingId='" + trackingId + '\'' +
                ", title='" + title + '\'' +
                ", brand='" + brand + '\'' +
                ", category='" + category + '\'' +
                ", productUpc='" + productUpc + '\'' +
                ", retailerSku='" + retailerSku + '\'' +
                ", discount='" + discount + '\'' +
                ", productImage='" + productImage + '\'' +
                '}'
    }

    @Deprecated("Use ProductUpc field instead.", ReplaceWith("productUpc"))
    fun getBarCode(): String {
        return productUpc
    }

    class Builder {
        private var trackingId = ""
        private var title = ""
        private var brand = ""
        private var category = ""
        private var productUpc = ""
        private var retailerSku = ""
        private var discount = ""
        private var productImage = ""
        fun setTrackingId(trackingId: String): Builder {
            this.trackingId = trackingId
            return this
        }

        fun setTitle(title: String): Builder {
            this.title = title
            return this
        }

        fun setBrand(brand: String): Builder {
            this.brand = brand
            return this
        }

        fun setCategory(category: String): Builder {
            this.category = category
            return this
        }

        fun setProductUpc(productUpc: String): Builder {
            this.productUpc = productUpc
            return this
        }

        fun setRetailerSku(retailerSku: String): Builder {
            this.retailerSku = retailerSku
            return this
        }

        fun setDiscount(discount: String): Builder {
            this.discount = discount
            return this
        }

        fun setProductImage(productImage: String): Builder {
            this.productImage = productImage
            return this
        }

        fun build(): AddToListItem {
            return AddToListItem(
                    trackingId,
                    title,
                    brand,
                    category,
                    productUpc,
                    retailerSku,
                    discount,
                    productImage)
        }

    }

    companion object CREATOR : Parcelable.Creator<AddToListItem> {
        override fun createFromParcel(parcel: Parcel): AddToListItem {
            return AddToListItem(parcel)
        }

        override fun newArray(size: Int): Array<AddToListItem?> {
            return arrayOfNulls(size)
        }
    }
}