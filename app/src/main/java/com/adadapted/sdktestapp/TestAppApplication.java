package com.adadapted.sdktestapp;

import android.app.Application;
import android.util.Log;

import com.adadapted.android.sdk.AdAdapted;
import com.adadapted.android.sdk.ui.messaging.AaSdkEventListener;
import com.facebook.stetho.Stetho;
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
        super.onCreate();

        // Create an InitializerBuilder
        Stetho.InitializerBuilder initializerBuilder = Stetho.newInitializerBuilder(this);

        // Enable Chrome DevTools
        initializerBuilder.enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this));

        // Enable command line interface
        initializerBuilder.enableDumpapp(Stetho.defaultDumperPluginsProvider(this));

        // Use the InitializerBuilder to generate an Initializer
        Stetho.Initializer initializer = initializerBuilder.build();

        // Initialize Stetho with the Initializer
        Stetho.initialize(initializer);

        NewRelic.withApplicationToken("AAabde692c32523732801586d2c793895d9dae5e0d").start(this);

        String apiKey = getResources().getString(R.string.adadapted_api_key);

        AdAdapted.init(this)
            .withAppId(apiKey)
            .inEnv(AdAdapted.Env.DEV)
            .setSdkEventListener(new AaSdkEventListener() {
                @Override
                public void onHasAdsToServe(boolean enabled) {
                    Log.i(TAG, "AdAdapted has Campaign: " + enabled);
                }

                @Override
                public void onNextAdEvent(String zoneId, String eventType) {
                    Log.i(TAG, "Ad " + eventType + " for Zone " + zoneId);
                }
            })
            .start();
    }
}
