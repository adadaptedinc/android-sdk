package com.adadapted.android.sdk.ext.factory;

import android.content.Context;

import com.adadapted.android.sdk.core.device.BuildDeviceInfoParam;
import com.adadapted.android.sdk.core.device.DeviceInfoBuilder;
import com.adadapted.android.sdk.core.device.model.DeviceInfo;

/**
 * Created by chrisweeden on 8/20/15.
 */
public class DeviceInfoFactory {
    private static final String LOGTAG = DeviceInfoFactory.class.getName();

    private static DeviceInfo sDeviceInfo;

    public static synchronized void createDeviceInfo(final Context context,
                                                     final String appId,
                                                     final String sdkVersion,
                                                     final boolean isProd,
                                                     final DeviceInfoBuilder.Listener listener) {
        DeviceInfoBuilder deviceInfoBuilder = new DeviceInfoBuilder();
        deviceInfoBuilder.execute(new BuildDeviceInfoParam(context, appId, sdkVersion, isProd));
        deviceInfoBuilder.addListener(new DeviceInfoBuilder.Listener() {
            @Override
            public void onDeviceInfoCollected(final DeviceInfo deviceInfo) {
                sDeviceInfo = deviceInfo;
                listener.onDeviceInfoCollected(deviceInfo);
            }
        });
    }

    public static synchronized DeviceInfo getsDeviceInfo() {
        return sDeviceInfo;
    }
}
