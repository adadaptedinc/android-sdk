package com.adadapted.android.sdk.core.atl;

import android.os.Parcel;
import android.os.Parcelable;

public final class AddToListItem implements Parcelable {
    public static final Creator<AddToListItem> CREATOR = new Creator<AddToListItem>() {
        @Override
        public AddToListItem createFromParcel(Parcel in) {
            return new AddToListItem(in);
        }

        @Override
        public AddToListItem[] newArray(int size) {
            return new AddToListItem[size];
        }
    };

    private final String trackingId;
    private final String title;
    private final String brand;
    private final String category;
    private final String productUpc;
    private final String retailerSku;
    private final String discount;
    private final String productImage;

    public AddToListItem(final String trackingId,
                         final String title,
                         final String brand,
                         final String category,
                         final String productUpc,
                         final String retailerSku,
                         final String discount,
                         final String productImage) {
        this.trackingId = trackingId;
        this.title = title;
        this.brand = brand;
        this.category = category;
        this.productUpc = productUpc;
        this.retailerSku = retailerSku;
        this.discount = discount;
        this.productImage = productImage;
    }

    private AddToListItem(Parcel in) {
        trackingId = in.readString();
        title = in.readString();
        brand = in.readString();
        category = in.readString();
        productUpc = in.readString();
        retailerSku = in.readString();
        discount = in.readString();
        productImage = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(trackingId);
        dest.writeString(title);
        dest.writeString(brand);
        dest.writeString(category);
        dest.writeString(productUpc);
        dest.writeString(retailerSku);
        dest.writeString(discount);
        dest.writeString(productImage);
    }

    public String getTrackingId() {
        return trackingId;
    }

    public String getTitle() {
        return title;
    }

    public String getBrand() {
        return brand;
    }

    public String getCategory() {
        return category;
    }

    @Deprecated
    public String getBarCode() {
        return productUpc;
    }

    public String getProductUpc() {
        return productUpc;
    }

    public String getRetailerSku() {
        return retailerSku;
    }

    public String getDiscount() {
        return discount;
    }

    public String getProductImage() {
        return productImage;
    }

    @Override
    public String toString() {
        return "AddToListItem{" +
                "trackingId='" + trackingId + '\'' +
                ", title='" + title + '\'' +
                ", brand='" + brand + '\'' +
                ", category='" + category + '\'' +
                ", productUpc='" + productUpc + '\'' +
                ", retailerSku='" + retailerSku + '\'' +
                ", discount='" + discount + '\'' +
                ", productImage='" + productImage + '\'' +
                '}';
    }

    public static class Builder {
        private String trackingId;
        private String title;
        private String brand;
        private String category;
        private String productUpc;
        private String retailerSku;
        private String discount;
        private String productImage;

        public Builder() {
            trackingId = "";
            title = "";
            brand = "";
            category = "";
            productUpc = "";
            retailerSku = "";
            discount = "";
            productImage = "";
        }

        public String getTrackingId() {
            return trackingId;
        }

        public Builder setTrackingId(String trackingId) {
            this.trackingId = trackingId;

            return this;
        }

        public String getTitle() {
            return title;
        }

        public Builder setTitle(String title) {
            this.title = title;

            return this;
        }

        public String getBrand() {
            return brand;
        }

        public Builder setBrand(String brand) {
            this.brand = brand;

            return this;
        }

        public String getCategory() {
            return category;
        }

        public Builder setCategory(String category) {
            this.category = category;

            return this;
        }

        public String getBarCode() {
            return productUpc;
        }

        public Builder setProductUpc(String productUpc) {
            this.productUpc = productUpc;

            return this;
        }

        public String getRetailerSku() {
            return retailerSku;
        }

        public Builder setRetailerSku(String retailerSku) {
            this.retailerSku = retailerSku;

            return this;
        }

        public String getDiscount() {
            return discount;
        }

        public Builder setDiscount(String discount) {
            this.discount = discount;

            return this;
        }

        public String getProductImage() {
            return productImage;
        }

        public Builder setProductImage(String productImage) {
            this.productImage = productImage;

            return this;
        }

        public AddToListItem build() {
            return new AddToListItem(
                    trackingId,
                    title,
                    brand,
                    category,
                    productUpc,
                    retailerSku,
                    discount,
                    productImage);
        }
    }
}
