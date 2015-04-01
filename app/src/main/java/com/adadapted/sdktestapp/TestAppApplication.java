package com.adadapted.sdktestapp;

import android.app.Application;

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
        AdAdapted.init(this, "TESTAPPLICATION1", new String[]{"10", "12"});
    }
}
