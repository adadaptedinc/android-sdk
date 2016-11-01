package com.adadapted.demo.baskethandle.plugins;

import android.content.Context;
import android.util.Log;

/**
 * Created by chrisweeden on 10/4/16.
 */

public class NewRelicPlugin implements Plugin {
    private static final String LOGTAG = NewRelicPlugin.class.getName();

    @Override
    public void initialize(final Context context) {
        Log.i(LOGTAG, "Initializing NewRelic SDK.");

    }
}
