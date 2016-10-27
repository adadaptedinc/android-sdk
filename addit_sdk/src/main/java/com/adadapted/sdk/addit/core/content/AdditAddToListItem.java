package com.adadapted.sdk.addit.core.content;

/**
 * Created by chrisweeden on 10/27/16.
 */

public final class AdditAddToListItem {
    private final String trackingId;
    private final String title;
    private final String brand;
    private final String category;
    private final String barCode;
    private final String discount;
    private final String productImage;

    AdditAddToListItem(final String trackingId,
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
        return "AdditAddToListItem{" +
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
