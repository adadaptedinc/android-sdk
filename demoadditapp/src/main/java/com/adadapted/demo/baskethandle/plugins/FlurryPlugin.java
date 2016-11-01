package com.adadapted.demo.baskethandle.plugins;

import android.content.Context;
import android.util.Log;

/**
 * Created by chrisweeden on 10/4/16.
 */

public class FlurryPlugin implements Plugin {
    private static final String LOGTAG = FlurryPlugin.class.getName();

    @Override
    public void initialize(final Context context) {
        Log.i(LOGTAG, "Initializing Flurry SDK.");

    }
}
