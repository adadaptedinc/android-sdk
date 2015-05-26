package com.adadapted.android.sdk.core.common;

/**
 * Created by chrisweeden on 3/26/15.
 */
public class Dimension {
    private int height;
    private int width;

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
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
