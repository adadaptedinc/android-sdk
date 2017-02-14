package com.adadapted.sdk.addit.core.content;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by chrisweeden on 10/27/16.
 */
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
}
