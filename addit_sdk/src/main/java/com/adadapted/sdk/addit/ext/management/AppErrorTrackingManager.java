package com.adadapted.sdk.addit.ext.management;

import com.adadapted.sdk.addit.config.Config;
import com.adadapted.sdk.addit.core.app.AppErrorSink;
import com.adadapted.sdk.addit.core.app.AppErrorTracker;
import com.adadapted.sdk.addit.core.app.RegisterAppErrorCommand;
import com.adadapted.sdk.addit.core.app.RegisterAppErrorInteractor;
import com.adadapted.sdk.addit.core.device.DeviceInfo;
import com.adadapted.sdk.addit.ext.concurrency.ThreadPoolInteractorExecuter;
import com.adadapted.sdk.addit.ext.http.HttpAppErrorSink;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by chrisweeden on 9/29/16.
 */
public class AppErrorTrackingManager implements DeviceInfoManager.Callback {
    private static AppErrorTrackingManager sInstance;

    private static AppErrorTrackingManager getInstance() {
        if(sInstance == null) {
            sInstance = new AppErrorTrackingManager();
        }

        return sInstance;
    }

    public static void registerEvent(final String errorCode,
                                     final String errorMessge,
                                     final Map<String, String> errorParams) {
        if(getInstance().tracker == null) {
            getInstance().addTempErrorItem(new AppErrorTrackingManager.TempErrorItem(errorCode, errorMessge, errorParams));
        }
        else {
            final TempErrorItem item = new TempErrorItem(errorCode, errorMessge, errorParams);
            getInstance().trackEvent(item);
        }
    }

    private final Set<TempErrorItem> tempErrorItems = new HashSet<>();
    private final Lock lock = new ReentrantLock();

    private AppErrorTracker tracker;

    private AppErrorTrackingManager() {
        DeviceInfoManager.getInstance().getDeviceInfo(this);
    }

    private void trackEvent(final TempErrorItem item) {
        final RegisterAppErrorCommand command = new RegisterAppErrorCommand(
                item.getErrorCode(),
                item.getErrorMessage(),
                item.getErrorParams());
        final RegisterAppErrorInteractor interactor = new RegisterAppErrorInteractor(
                command,
                tracker);

        ThreadPoolInteractorExecuter.getInstance().executeInBackground(interactor);
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

    @Override
    public void onDeviceInfoCollected(final DeviceInfo deviceInfo) {
        DeviceInfoManager.getInstance().removeCallback(this);

        final String endpoint = determineEndpoint(deviceInfo);
        final AppErrorSink sink = new HttpAppErrorSink(endpoint);

        tracker = new AppErrorTracker(deviceInfo, sink);

        trackTempErrorItems();
    }

    private String determineEndpoint(final DeviceInfo deviceInfo) {
        if(deviceInfo.isProd()) {
            return Config.Prod.URL_APP_ERROR_TRACK;
        }

        return Config.Dev.URL_APP_ERROR_TRACK;
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
