package com.adadapted.android.sdk.ext;

import android.content.Context;

import com.adadapted.android.sdk.config.Config;
import com.adadapted.android.sdk.core.ad.AdEventClient;
import com.adadapted.android.sdk.core.addit.PayloadClient;
import com.adadapted.android.sdk.core.event.AppEventClient;
import com.adadapted.android.sdk.core.intercept.InterceptClient;
import com.adadapted.android.sdk.core.session.SessionClient;
import com.adadapted.android.sdk.ext.http.HttpAdEventSink;
import com.adadapted.android.sdk.ext.http.HttpAppEventSink;
import com.adadapted.android.sdk.ext.http.HttpInterceptAdapter;
import com.adadapted.android.sdk.ext.http.HttpPayloadAdapter;
import com.adadapted.android.sdk.ext.http.HttpRequestManager;
import com.adadapted.android.sdk.ext.http.HttpSessionAdapter;

public class WireUp {
    public static void run(final Context context, final boolean isProd) {
        HttpRequestManager.createQueue(context.getApplicationContext());

        SessionClient.createInstance(new HttpSessionAdapter(
            Config.initializeSessionUrl(isProd),
            Config.refreshAdsUrl(isProd)
        ));

        AppEventClient.createInstance(new HttpAppEventSink(
            Config.appEventsUrl(isProd),
            Config.appErrorsUrl(isProd)
        ));

        AdEventClient.createInstance(new HttpAdEventSink(
            Config.adEventsUrl(isProd)
        ));

        InterceptClient.createInstance(
            new HttpInterceptAdapter(
                Config.retrieveInterceptsUrl(isProd),
                Config.interceptEventsUrl(isProd)
            )
        );

        PayloadClient.createInstance(new HttpPayloadAdapter(
            Config.pickupPayloadsUrl(isProd),
            Config.trackPayloadUrl(isProd)
        ));
    }
}
