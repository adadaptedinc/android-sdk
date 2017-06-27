package com.adadapted.android.sdk.ext.management;

import com.adadapted.android.sdk.config.Config;
import com.adadapted.android.sdk.core.device.DeviceInfo;
import com.adadapted.android.sdk.core.event.AppErrorSink;
import com.adadapted.android.sdk.core.event.AppErrorTracker;
import com.adadapted.android.sdk.core.event.RegisterAppErrorCommand;
import com.adadapted.android.sdk.core.event.RegisterAppErrorInteractor;
import com.adadapted.android.sdk.ext.concurrency.ThreadPoolInteractorExecuter;
import com.adadapted.android.sdk.ext.http.HttpAppErrorSink;
import com.adadapted.android.sdk.ext.json.JsonAppErrorBuilder;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class AppErrorTrackingManager implements DeviceInfoManager.Callback {
    private static AppErrorTrackingManager sInstance;

    private static synchronized AppErrorTrackingManager getInstance() {
        if(sInstance == null) {
            sInstance = new AppErrorTrackingManager();
        }

        return sInstance;
    }

    public static synchronized void registerEvent(final String errorCode,
                                                  final String errorMessage) {
        registerEvent(errorCode, errorMessage, new HashMap<String, String>());
    }

    public static synchronized void registerEvent(final String errorCode,
                                                  final String errorMessage,
                                                  final Map<String, String> errorParams) {
        final TempErrorItem item = new TempErrorItem(errorCode, errorMessage, errorParams);
        getInstance().trackEvent(item);
    }

    private final Set<TempErrorItem> tempErrorItems = new HashSet<>();
    private final Lock lock = new ReentrantLock();

    private AppErrorTracker tracker;

    private AppErrorTrackingManager() {
        DeviceInfoManager.getInstance().getDeviceInfo(this);
    }

    private void addTempErrorItem(final TempErrorItem item) {
        lock.lock();
        try {
            tempErrorItems.add(item);
        } finally {
            lock.unlock();
        }
    }

    private void trackTempErrorItems() {
        lock.lock();
        try {
            final Set<TempErrorItem> errorItems = new HashSet<>(tempErrorItems);
            tempErrorItems.clear();

            for(final TempErrorItem i : errorItems) {
                trackEvent(i);
            }
        } finally {
            lock.unlock();
        }
    }

    private void trackEvent(final TempErrorItem item) {
        if(tracker == null) {
            addTempErrorItem(item);
            return;
        }

        final RegisterAppErrorCommand command = new RegisterAppErrorCommand(
            item.getErrorCode(),
            item.getErrorMessage(),
            item.getErrorParams()
        );

        final RegisterAppErrorInteractor interactor = new RegisterAppErrorInteractor(
                command,
                tracker);

        ThreadPoolInteractorExecuter.getInstance().executeInBackground(interactor);
    }

    @Override
    public void onDeviceInfoCollected(final DeviceInfo deviceInfo) {
        DeviceInfoManager.getInstance().removeCallback(this);

        final String endpoint = determineEndpoint(deviceInfo);
        final AppErrorSink sink = new HttpAppErrorSink(endpoint);

        tracker = new AppErrorTracker(deviceInfo, sink, new JsonAppErrorBuilder());

        trackTempErrorItems();
    }

    private String determineEndpoint(final DeviceInfo deviceInfo) {
        if(deviceInfo.isProd()) {
            return Config.Prod.URL_APP_ERROR_TRACK;
        }

        return Config.Sand.URL_APP_ERROR_TRACK;
    }

    private static class TempErrorItem {
        private final String errorCode;
        private final String errorMessage;
        private final Map<String, String> errorParams;

        TempErrorItem(final String errorCode,
                      final String errorMessage,
                      final Map<String, String> errorParams) {
            this.errorCode = errorCode == null ? "unknown" : errorCode;
            this.errorMessage = errorMessage == null ? "unknown" : errorMessage;
            this.errorParams = errorParams == null ? new HashMap<String, String>() : errorParams;
        }

        String getErrorCode() {
            return errorCode;
        }

        String getErrorMessage() {
            return errorMessage;
        }

        Map<String, String> getErrorParams() {
            return errorParams;
        }
    }
}
