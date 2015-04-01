package com.adadapted.android.sdk;

/**
 * Created by chrisweeden on 3/26/15.
 */
class Dimension {
    private int height;
    private int width;

    int getHeight() {
        return height;
    }

    void setHeight(int height) {
        this.height = height;
    }

    int getWidth() {
        return width;
    }

    void setWidth(int width) {
        this.width = width;
    }

    @Override
    public String toString() {
        return "ZoneDimension{" +
                "height=" + height +
                ", width=" + width +
                '}';
    }
}
