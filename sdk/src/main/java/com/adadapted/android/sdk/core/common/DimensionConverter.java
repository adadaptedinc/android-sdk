package com.adadapted.android.sdk.core.common;

/**
 * Created by chrisweeden on 7/20/15.
 */
public class DimensionConverter {
    private final float scale;

    public DimensionConverter(final float scale) {
        this.scale = scale;
    }

    public int convertDpToPx(final int dpValue) {
        if(dpValue > 0) {
            return (int) (dpValue * scale + 0.5f);
        }

        return dpValue;
    }
}
