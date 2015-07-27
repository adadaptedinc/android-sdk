package com.adadapted.android.sdk.ext.factory;

import android.content.Context;

import com.adadapted.android.sdk.AdAdapted;
import com.adadapted.android.sdk.config.Config;
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
    private static KeywordInterceptManagerFactory instance;

    public static synchronized KeywordInterceptManagerFactory getInstance() {
        if(instance == null) {
            instance = new KeywordInterceptManagerFactory();
        }

        return instance;
    }

    private KeywordInterceptManager interceptManager;

    private KeywordInterceptManagerFactory() {}

    public KeywordInterceptManager createKeywordInterceptManager(Context context) {
        if(interceptManager == null) {
            KeywordInterceptAdapter adapter = new HttpKeywordInterceptAdapter(determineInitEndpoint(),
                    determineTrackEndpoint());
            KeywordInterceptBuilder builder = new JsonKeywordInterceptBuilder();
            KeywordInterceptRequestBuilder requestBuilder = new JsonKeywordInterceptRequestBuilder();

            interceptManager = new KeywordInterceptManager(adapter, builder, requestBuilder);
        }

        return interceptManager;
    }

    private String determineInitEndpoint() {
        if(AdAdapted.getInstance().isProd()) {
            return Config.Prod.URL_KI_INIT;
        }

        return Config.Sand.URL_KI_INIT;
    }

    private String determineTrackEndpoint() {
        if(AdAdapted.getInstance().isProd()) {
            return Config.Prod.URL_KI_TRACK;
        }

        return Config.Sand.URL_KI_TRACK;
    }
}
