package com.adadapted.android.sdk.ext;

import android.content.Context;

import com.adadapted.android.sdk.config.Config;
import com.adadapted.android.sdk.core.ad.AdEventClient;
import com.adadapted.android.sdk.core.addit.PayloadClient;
import com.adadapted.android.sdk.core.event.AppEventClient;
import com.adadapted.android.sdk.core.keywordintercept.KeywordInterceptClient;
import com.adadapted.android.sdk.core.session.SessionClient;
import com.adadapted.android.sdk.ext.http.HttpAdEventSink;
import com.adadapted.android.sdk.ext.http.HttpAppEventSink;
import com.adadapted.android.sdk.ext.http.HttpKeywordInterceptAdapter;
import com.adadapted.android.sdk.ext.http.HttpPayloadAdapter;
import com.adadapted.android.sdk.ext.http.HttpRequestManager;
import com.adadapted.android.sdk.ext.http.HttpSessionAdapter;

public class WireUp {
    public static void run(final Context context, final boolean isProd) {
        HttpRequestManager.createQueue(context.getApplicationContext());

        SessionClient.createInstance(new HttpSessionAdapter(
            isProd ? Config.Prod.URL_SESSION_INIT : Config.Sand.URL_SESSION_INIT,
            isProd ? Config.Prod.URL_AD_GET : Config.Sand.URL_AD_GET
        ));

        AppEventClient.createInstance(new HttpAppEventSink(
            isProd ? Config.Prod.URL_APP_EVENT_TRACK : Config.Sand.URL_APP_EVENT_TRACK,
            isProd ? Config.Prod.URL_APP_ERROR_TRACK : Config.Sand.URL_APP_ERROR_TRACK
        ));

        AdEventClient.createInstance(new HttpAdEventSink(
            isProd ? Config.Prod.URL_EVENT_BATCH : Config.Sand.URL_EVENT_BATCH
        ));

        KeywordInterceptClient.createInstance(
            new HttpKeywordInterceptAdapter(
                isProd ? Config.Prod.URL_KI_INIT : Config.Sand.URL_KI_INIT,
                isProd ? Config.Prod.URL_KI_TRACK : Config.Sand.URL_KI_TRACK
            )
        );

        PayloadClient.createInstance(new HttpPayloadAdapter(
            isProd ? Config.Prod.URL_APP_PAYLOAD_PICKUP : Config.Sand.URL_APP_PAYLOAD_PICKUP,
            isProd ? Config.Prod.URL_APP_PAYLOAD_TRACK : Config.Sand.URL_APP_PAYLOAD_TRACK
        ));
    }
}
