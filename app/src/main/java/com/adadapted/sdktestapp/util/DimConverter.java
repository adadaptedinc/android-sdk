package com.adadapted.sdktestapp.util;

import android.content.Context;

/**
 * Created by chrisweeden on 7/20/15.
 */
public class DimConverter {
    public static int convertDpToPx(Context context, int dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
