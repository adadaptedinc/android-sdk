package com.adadapted.sdk.demoadvertisingapp;

import android.app.Application;
import android.util.Log;

import com.adadapted.android.sdk.AdAdapted;
import com.adadapted.android.sdk.ui.messaging.AaSdkEventListener;
import com.adadapted.android.sdk.ui.messaging.AaSdkSessionListener;

public class DemoAdvertisingApplication extends Application {
    private static final String TAG = DemoAdvertisingApplication.class.getName();

    @Override
    public void onCreate() {
        super.onCreate();

        String apiKey = getResources().getString(R.string.adadapted_api_key);

        AdAdapted.init()
                .withAppId(apiKey)
                .inEnv(AdAdapted.Env.DEV)
                .setSdkSessionListener(new AaSdkSessionListener() {
                    @Override
                    public void onHasAdsToServe(boolean hasAds) {
                        Log.i(TAG, "Has Ads To Serve: " + hasAds);
                    }
                })
                .setSdkEventListener(new AaSdkEventListener() {
                    @Override
                    public void onNextAdEvent(String zoneId, String eventType) {
                        Log.i(TAG, "Ad " + eventType + " for Zone " + zoneId);
                    }
                })
                .start(this);
    }
}
