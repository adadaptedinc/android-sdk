package com.adadapted.sdk.addit.core.device;

import android.content.Context;

import com.adadapted.sdk.addit.core.common.Command;

/**
 * Created by chrisweeden on 9/26/16.
 */

public class CollectDeviceInfoCommand extends Command {
    private final Context context;
    private final String appId;
    private final boolean isProd;
    private final String sdkVersion;

    public CollectDeviceInfoCommand(final Context context,
                                    final String appId,
                                    final boolean isProd,
                                    final String sdkVersion) {
        this.context = context;
        this.appId = appId;
        this.isProd = isProd;
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

    public String getSdkVersion() {
        return sdkVersion;
    }
}
