package com.adadapted.android.sdk.core.device;

import android.content.Context;

/**
 * Created by chrisweeden on 3/26/15.
 */
public class BuildDeviceInfoParam {
    private final Context mContext;
    private final String mAppId;
    private final String mSdkVersion;
    private final boolean mIsProd;

    public BuildDeviceInfoParam(final Context context,
                                final String appId,
                                final String sdkVersion,
                                final boolean isProd) {
        mContext = context;
        mAppId = appId;
        mSdkVersion = sdkVersion;
        mIsProd = isProd;
    }

    public Context getContext() {
        return mContext;
    }

    public String getAppId() {
        return mAppId;
    }

    public String getSdkVersion() {
        return mSdkVersion;
    }

    public boolean isProd() {
        return mIsProd;
    }

    @Override
    public String toString() {
        return "BuildDeviceInfoParam{" +
                "context=" + mContext +
                ", appId='" + mAppId + '\'' +
                ", sdkVersion='" + mSdkVersion + '\'' +
                ", isProd=" + mIsProd +
                '}';
    }
}
