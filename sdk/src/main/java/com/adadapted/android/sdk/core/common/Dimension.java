package com.adadapted.android.sdk.core.common;

/**
 * Created by chrisweeden on 3/26/15.
 */
public class Dimension {
    public static class ORIEN {
        public static final String LAND = "land";
        public static final String PORT = "port";
    }

    public static final int MATCH_PARENT = -1;
    public static final int WRAP_CONTENT = -2;

    private int height = 0;
    private int width = 0;

    public int getHeight() {
        return height;
    }

    public void setHeight(final int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(final int width) {
        this.width = width;
    }
}
