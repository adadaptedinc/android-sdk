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
    private final String barCode;
    private final String discount;
    private final String productImage;

    public AddToListItem(final String trackingId,
                         final String title,
                         final String brand,
                         final String category,
                         final String barCode,
                         final String discount,
                         final String productImage) {
        this.trackingId = trackingId;
        this.title = title;
        this.brand = brand;
        this.category = category;
        this.barCode = barCode;
        this.discount = discount;
        this.productImage = productImage;
    }

    private AddToListItem(Parcel in) {
        trackingId = in.readString();
        title = in.readString();
        brand = in.readString();
        category = in.readString();
        barCode = in.readString();
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
        dest.writeString(barCode);
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

    public String getBarCode() {
        return barCode;
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
                ", barCode='" + barCode + '\'' +
                ", discount='" + discount + '\'' +
                ", productImage='" + productImage + '\'' +
                '}';
    }

    public static class Builder {
        private String trackingId;
        private String title;
        private String brand;
        private String category;
        private String barCode;
        private String discount;
        private String productImage;

        public Builder() {
            trackingId = "";
            title = "";
            brand = "";
            category = "";
            barCode = "";
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
            return barCode;
        }

        public Builder setBarCode(String barCode) {
            this.barCode = barCode;

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
            return new AddToListItem(trackingId, title, brand, category, barCode, discount, productImage);
        }
    }
}
