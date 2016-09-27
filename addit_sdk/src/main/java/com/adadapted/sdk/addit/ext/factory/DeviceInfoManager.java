package com.adadapted.sdk.addit.ext.factory;

import android.content.Context;

import com.adadapted.sdk.addit.core.common.Interactor;
import com.adadapted.sdk.addit.core.config.Config;
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
    private Set<Callback> callbacks;

    public DeviceInfoManager() {
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

    public void addCallback(final Callback callback) {
        callbacks.add(callback);

        if(deviceInfo != null) {
            callback.onDeviceInfoCollected(deviceInfo);
        }
    }

    public void removeCallback(final Callback callback) {
        callbacks.remove(callback);
    }

    @Override
    public void onDeviceInfoCollected(final DeviceInfo deviceInfo) {
        this.deviceInfo = deviceInfo;

        for(Callback callback : callbacks) {
            callback.onDeviceInfoCollected(deviceInfo);
        }
    }

    @Override
    public void onDeviceInfoCollectionError(final Throwable e) {
        for(Callback callback : callbacks) {
            callback.onDeviceInfoCollectionError(e);
        }
    }

    public interface Callback {
        void onDeviceInfoCollected(DeviceInfo deviceInfo);
        void onDeviceInfoCollectionError(Throwable e);
    }
}
