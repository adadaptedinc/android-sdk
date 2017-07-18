package com.adadapted.android.sdk.core.common;

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
