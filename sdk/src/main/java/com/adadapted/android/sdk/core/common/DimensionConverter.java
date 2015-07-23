package com.adadapted.android.sdk.core.common;

import android.content.Context;

/**
 * Created by chrisweeden on 7/20/15.
 */
public class DimensionConverter {
    public static int convertDpToPx(Context context, int dpValue) {
        if(dpValue > 0) {
            final float scale = context.getResources().getDisplayMetrics().density;
            return (int) (dpValue * scale + 0.5f);
        }

        return dpValue;
    }
}
