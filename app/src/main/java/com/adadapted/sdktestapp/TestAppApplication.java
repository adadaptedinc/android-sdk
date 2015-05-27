package com.adadapted.sdktestapp;

import android.app.Application;
import android.util.Log;

import com.adadapted.android.sdk.AdAdapted;
import com.newrelic.agent.android.NewRelic;

/**
 * Created by chrisweeden on 3/16/15.
 */
public class TestAppApplication extends Application {
    private static final String TAG = TestAppApplication.class.getName();

    public TestAppApplication() {
        super();
    }

    @Override
    public void onCreate() {
        NewRelic.withApplicationToken("AAabde692c32523732801586d2c793895d9dae5e0d").start(this);

        String apiKey = getResources().getString(R.string.adadapted_api_key);
        String[] zones = getResources().getStringArray(R.array.adadapted_zones);
        boolean isProd = getResources().getBoolean(R.bool.adadapted_prod);

        AdAdapted.init(this, apiKey, zones, isProd);
    }
}
