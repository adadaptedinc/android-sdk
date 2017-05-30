package com.adadapted.android.sdk.ext.management;

import android.content.Context;

import com.adadapted.android.sdk.config.Config;
import com.adadapted.android.sdk.core.common.Interactor;
import com.adadapted.android.sdk.core.device.CollectDeviceInfoCommand;
import com.adadapted.android.sdk.core.device.CollectDeviceInfoInteractor;
import com.adadapted.android.sdk.core.device.DeviceInfo;
import com.adadapted.android.sdk.ext.concurrency.ThreadPoolInteractorExecuter;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by chrisweeden on 9/26/16.
 */

public class DeviceInfoManager implements CollectDeviceInfoInteractor.Callback {
    private static DeviceInfoManager sInstance;

    public static synchronized DeviceInfoManager getInstance() {
        if(sInstance == null) {
            sInstance = new DeviceInfoManager();
        }

        return sInstance;
    }

    private DeviceInfo deviceInfo;
    final private Set<Callback> callbacks;

    private DeviceInfoManager() {
        callbacks = new HashSet<>();
    }

    public void collectDeviceInfo(final Context context,
                                  final String appId,
                                  final boolean isProd,
                                  final Callback callback) {
        addCallback(callback);

        final Interactor interactor = new CollectDeviceInfoInteractor(
            new CollectDeviceInfoCommand(
                    context.getApplicationContext(),
                    appId,
                    isProd,
                    Config.SDK_VERSION),
            this
        );

        ThreadPoolInteractorExecuter.getInstance().executeInBackground(interactor);
    }

    public void getDeviceInfo(final Callback callback) {
        addCallback(callback);
    }

    private void addCallback(final Callback callback) {
        callbacks.add(callback);

        if(deviceInfo != null) {
            callback.onDeviceInfoCollected(deviceInfo);
        }
    }

    @Override
    public void onDeviceInfoCollected(final DeviceInfo deviceInfo) {
        this.deviceInfo = deviceInfo;

        final Set<Callback> currentCallbacks = new HashSet<>(callbacks);
        for(Callback callback : currentCallbacks) {
            callback.onDeviceInfoCollected(deviceInfo);
        }
    }

    public interface Callback {
        void onDeviceInfoCollected(DeviceInfo deviceInfo);
    }
}
