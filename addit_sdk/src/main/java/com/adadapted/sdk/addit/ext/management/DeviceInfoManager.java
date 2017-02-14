package com.adadapted.sdk.addit.ext.management;

import android.content.Context;

import com.adadapted.sdk.addit.core.common.Interactor;
import com.adadapted.sdk.addit.config.Config;
import com.adadapted.sdk.addit.core.device.CollectDeviceInfoCommand;
import com.adadapted.sdk.addit.core.device.CollectDeviceInfoInteractor;
import com.adadapted.sdk.addit.core.device.DeviceInfo;
import com.adadapted.sdk.addit.ext.concurrency.ThreadPoolInteractorExecuter;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by chrisweeden on 9/26/16.
 */
public class DeviceInfoManager implements CollectDeviceInfoInteractor.Callback {
    private static DeviceInfoManager sInstance;

    public static DeviceInfoManager getInstance() {
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
            new CollectDeviceInfoCommand(context, appId, isProd, Config.SDK_VERSION),
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
