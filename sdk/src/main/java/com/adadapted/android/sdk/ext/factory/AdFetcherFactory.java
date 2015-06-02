package com.adadapted.android.sdk.ext.factory;

import android.content.Context;

import com.adadapted.android.sdk.AdAdapted;
import com.adadapted.android.sdk.R;
import com.adadapted.android.sdk.core.ad.AdFetcher;
import com.adadapted.android.sdk.core.ad.AdRefreshBuilder;
import com.adadapted.android.sdk.ext.http.HttpAdAdapter;
import com.adadapted.android.sdk.ext.json.JsonAdRequestBuilder;

/**
 * Created by chrisweeden on 5/26/15.
 */
public class AdFetcherFactory {
    private static final String TAG = AdFetcherFactory.class.getName();

    private static AdFetcherFactory instance;

    public static synchronized AdFetcherFactory getInstance(Context context) {
        if(instance == null) {
            instance = new AdFetcherFactory(context);
        }

        return instance;
    }

    private Context context;
    private AdFetcher adFetcher;

    private AdFetcherFactory(Context context) {
        this.context = context;
    }

    public AdFetcher createAdFetcher() {
        if(adFetcher == null) {
            adFetcher = new AdFetcher(new HttpAdAdapter(determineEndpoint()),
                    new JsonAdRequestBuilder(),
                    new AdRefreshBuilder());
        }

        return adFetcher;
    }

    private String determineEndpoint() {
        if(AdAdapted.getInstance().isProd()) {
            return context.getString(R.string.prod_event_batch_object_url);
        }

        return context.getString(R.string.sandbox_event_batch_object_url);
    }
}
