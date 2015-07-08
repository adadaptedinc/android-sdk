package com.adadapted.android.sdk.ext.factory;

import android.content.Context;

import com.adadapted.android.sdk.AdAdapted;
import com.adadapted.android.sdk.R;
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

    public static synchronized KeywordInterceptManagerFactory getInstance(Context context) {
        if(instance == null) {
            instance = new KeywordInterceptManagerFactory(context);
        }

        return instance;
    }

    private final Context context;
    private KeywordInterceptManager interceptManager;

    private KeywordInterceptManagerFactory(Context context) {
        this.context = context;
    }

    public KeywordInterceptManager createKeywordInterceptManager() {
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
            return context.getString(R.string.prod_autofill_init_object_url);
        }

        return context.getString(R.string.sandbox_autofill_init_object_url);
    }

    private String determineTrackEndpoint() {
        if(AdAdapted.getInstance().isProd()) {
            return context.getString(R.string.prod_autofill_track_object_url);
        }

        return context.getString(R.string.sandbox_autofill_track_object_url);
    }
}
