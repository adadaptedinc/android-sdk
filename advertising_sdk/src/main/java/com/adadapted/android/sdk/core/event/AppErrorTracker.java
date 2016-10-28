package com.adadapted.android.sdk.core.event;

import android.util.Log;

import com.adadapted.android.sdk.core.device.DeviceInfo;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by chrisweeden on 9/29/16.
 */

public class AppErrorTracker {
    private static final String LOGTAG = AppErrorTracker.class.getName();

    private final JSONObject errorWrapper;
    private final AppErrorSink sink;
    private final AppErrorBuilder builder;

    public AppErrorTracker(final DeviceInfo deviceInfo,
                           final AppErrorSink sink,
                           final AppErrorBuilder builder) {
        this.sink = sink;
        this.builder = builder;

        this.errorWrapper = builder.buildWrapper(deviceInfo);
    }

    public void trackError(final String errorCode,
                           final String errorMessage,
                           final Map<String, String> errorParams) {
        Log.w(LOGTAG, "Tracking Error: " + errorCode + ": " + errorMessage);

        final JSONObject json = builder.buildItem(errorWrapper, errorCode, errorMessage, errorParams);
        sink.publishError(json);
    }
}
