package com.adadapted.android.sdk.core.device;

import android.content.Context;

import com.adadapted.android.sdk.core.common.Command;

import java.util.Map;

/**
 * Created by chrisweeden on 9/26/16.
 */

public class CollectDeviceInfoCommand extends Command {
    private final Context context;
    private final String appId;
    private final boolean isProd;
    private final Map<String, String> params;
    private final String sdkVersion;

    public CollectDeviceInfoCommand(final Context context,
                                    final String appId,
                                    final boolean isProd,
                                    final Map<String, String> params,
                                    final String sdkVersion) {
        this.context = context;
        this.appId = appId;
        this.isProd = isProd;
        this.params = params;
        this.sdkVersion = sdkVersion;
    }

    public Context getContext() {
        return context;
    }

    public String getAppId() {
        return appId;
    }

    public boolean isProd() {
        return isProd;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public String getSdkVersion() {
        return sdkVersion;
    }
}
