package com.adadapted.android.sdk.core.device;

import android.content.Context;

import java.util.Arrays;

/**
 * Created by chrisweeden on 3/26/15.
 */
public class BuildDeviceInfoParam {
    private final Context context;
    private final String appId;
    private final String[] zones;
    private final String sdkVersion;

    public BuildDeviceInfoParam(Context context, String appId, String[] zones, String sdkVersion) {
        this.context = context;
        this.appId = appId;
        this.zones = zones;
        this.sdkVersion = sdkVersion;
    }

    public Context getContext() {
        return context;
    }

    public String getAppId() {
        return appId;
    }

    public String[] getZones() {
        return zones;
    }

    public String getSdkVersion() {
        return sdkVersion;
    }

    @Override
    public String toString() {
        return "BuildDeviceInfoParam{" +
                "context=" + context +
                ", appId='" + appId + '\'' +
                ", zones=" + Arrays.toString(zones) +
                ", sdkVersion='" + sdkVersion + '\'' +
                '}';
    }
}
