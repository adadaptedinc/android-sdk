package com.adadapted.android.sdk.ext.factory;

import com.adadapted.android.sdk.config.Config;
import com.adadapted.android.sdk.core.ad.AdFetcher;
import com.adadapted.android.sdk.core.device.model.DeviceInfo;
import com.adadapted.android.sdk.ext.http.HttpAdAdapter;
import com.adadapted.android.sdk.ext.json.JsonAdRefreshBuilder;
import com.adadapted.android.sdk.ext.json.JsonAdRequestBuilder;
import com.adadapted.android.sdk.ext.json.JsonZoneBuilder;

/**
 * Created by chrisweeden on 5/26/15.
 */
public class AdFetcherFactory {
    private static final String LOGTAG = AdFetcherFactory.class.getName();

    private static AdFetcherFactory sInstance;

    private AdFetcher mAdFetcher;

    private AdFetcherFactory(final DeviceInfo deviceInfo) {
        mAdFetcher = new AdFetcher(new HttpAdAdapter(determineEndpoint(deviceInfo)),
                new JsonAdRequestBuilder(),
                new JsonAdRefreshBuilder(new JsonZoneBuilder(deviceInfo.getScale())));
    }

    public static AdFetcher createAdFetcher(final DeviceInfo deviceInfo) {
        if(sInstance == null) {
            sInstance = new AdFetcherFactory(deviceInfo);
        }

        return sInstance.mAdFetcher;
    }

    private String determineEndpoint(final DeviceInfo deviceInfo) {
        if(deviceInfo.isProd()) {
            return Config.Prod.URL_AD_GET;
        }

        return Config.Sand.URL_AD_GET;
    }
}
