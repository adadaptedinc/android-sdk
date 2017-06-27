package com.adadapted.android.sdk.ext.management;

import android.content.Context;

import com.adadapted.android.sdk.config.Config;
import com.adadapted.android.sdk.core.common.Interactor;
import com.adadapted.android.sdk.core.device.CollectDeviceInfoCommand;
import com.adadapted.android.sdk.core.device.CollectDeviceInfoInteractor;
import com.adadapted.android.sdk.core.device.DeviceInfo;
import com.adadapted.android.sdk.ext.concurrency.ThreadPoolInteractorExecuter;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DeviceInfoManager implements CollectDeviceInfoInteractor.Callback {
    @SuppressWarnings("unused")
    private static final String LOGTAG = DeviceInfoManager.class.getName();

    private static DeviceInfoManager sInstance;

    public static synchronized DeviceInfoManager getInstance() {
        if(sInstance == null) {
            sInstance = new DeviceInfoManager();
        }

        return sInstance;
    }

    private DeviceInfo deviceInfo;
    final private Set<Callback> callbacks;
    private final Lock lock = new ReentrantLock();

    private DeviceInfoManager() {
        callbacks = new HashSet<>();
    }

    public void collectDeviceInfo(final Context context,
                                  final String appId,
                                  final boolean isProd,
                                  final Map<String, String> params,
                                  final Callback callback) {
        addCallback(callback);

        final Interactor interactor = new CollectDeviceInfoInteractor(
            new CollectDeviceInfoCommand(
                    context.getApplicationContext(),
                    appId,
                    isProd,
                    params,
                    Config.SDK_VERSION),
            this
        );

        ThreadPoolInteractorExecuter.getInstance().executeInBackground(interactor);
    }

    public void getDeviceInfo(final Callback callback) {
        addCallback(callback);
    }

    private void addCallback(final Callback callback) {
        if(callback == null) {
            return;
        }

        lock.lock();
        try {
            callbacks.add(callback);
        } finally {
            lock.unlock();
        }

        if(deviceInfo != null) {
            callback.onDeviceInfoCollected(deviceInfo);
        }
    }

    void removeCallback(final Callback callback) {
        if(callback == null) {
            return;
        }

        lock.lock();
        try {
            callbacks.remove(callback);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void onDeviceInfoCollected(final DeviceInfo deviceInfo) {
        this.deviceInfo = deviceInfo;

        lock.lock();
        try {
            final Set<Callback> currentCallbacks = new HashSet<>(callbacks);
            for(final Callback callback : currentCallbacks) {
                callback.onDeviceInfoCollected(deviceInfo);
            }
        } finally {
            lock.unlock();
        }
    }

    public interface Callback {
        void onDeviceInfoCollected(DeviceInfo deviceInfo);
    }
}
