package com.adadapted.android.sdk.ext.factory;

import com.adadapted.android.sdk.config.Config;
import com.adadapted.android.sdk.core.device.model.DeviceInfo;
import com.adadapted.android.sdk.core.keywordintercept.KeywordInterceptAdapter;
import com.adadapted.android.sdk.core.keywordintercept.KeywordInterceptBuilder;
import com.adadapted.android.sdk.core.keywordintercept.KeywordInterceptRequestBuilder;
import com.adadapted.android.sdk.core.keywordintercept.KeywordInterceptManager;
import com.adadapted.android.sdk.ext.http.HttpKeywordInterceptAdapter;
import com.adadapted.android.sdk.ext.json.JsonKeywordInterceptBuilder;
import com.adadapted.android.sdk.ext.json.JsonKeywordInterceptRequestBuilder;

/**
 * Created by chrisweeden on 6/23/15.
 */
public class KeywordInterceptManagerFactory {
    private static KeywordInterceptManagerFactory sInstance;

    private KeywordInterceptManager mInterceptManager;

    private KeywordInterceptManagerFactory(DeviceInfo deviceInfo) {
        KeywordInterceptAdapter adapter = new HttpKeywordInterceptAdapter(
                determineInitEndpoint(deviceInfo),
                determineTrackEndpoint(deviceInfo)
        );

        KeywordInterceptBuilder builder = new JsonKeywordInterceptBuilder();
        KeywordInterceptRequestBuilder requestBuilder = new JsonKeywordInterceptRequestBuilder();

        mInterceptManager = new KeywordInterceptManager(adapter, builder, requestBuilder);
    }

    public static KeywordInterceptManager createKeywordInterceptManager(DeviceInfo deviceInfo) {
        if(sInstance == null) {
            sInstance = new KeywordInterceptManagerFactory(deviceInfo);
        }

        return sInstance.mInterceptManager;
    }

    private String determineInitEndpoint(DeviceInfo deviceInfo) {
        if(deviceInfo.isProd()) {
            return Config.Prod.URL_KI_INIT;
        }

        return Config.Sand.URL_KI_INIT;
    }

    private String determineTrackEndpoint(DeviceInfo deviceInfo) {
        if(deviceInfo.isProd()) {
            return Config.Prod.URL_KI_TRACK;
        }

        return Config.Sand.URL_KI_TRACK;
    }
}
