package com.adadapted.android.sdk.core.event;

import com.adadapted.android.sdk.core.concurrency.ThreadPoolInteractorExecuter;

import java.util.HashMap;
import java.util.Map;

public class AppEventClient {
    private static final String LOGTAG = AppEventClient.class.getName();

    private static class Types {
        static final String SDK = "sdk";
        static final String APP = "app";
    }

    private static AppEventClient instance;

    public static AppEventClient createInstance(final AppEventSink sink) {
        if(instance == null) {
            instance = new AppEventClient(sink);
        }

        return instance;
    }

    private static AppEventClient getInstance() {
        return instance;
    }

    public static synchronized void trackAppEvent(final String name,
                                                  final Map<String, String> params) {
        if(instance == null) {
            return;
        }

        ThreadPoolInteractorExecuter.getInstance().executeInBackground(new Runnable(){
            @Override
            public void run() {
                getInstance().performTrackEvent(Types.APP, name, params);
            }
        });
    }

    public static synchronized void trackAppEvent(final String name) {
        trackAppEvent(name, new HashMap<String, String>());
    }

    public static synchronized void trackSdkEvent(final String name,
                                                  final Map<String, String> params) {
        if(instance == null) {
            return;
        }

        ThreadPoolInteractorExecuter.getInstance().executeInBackground(new Runnable(){
            @Override
            public void run() {
                getInstance().performTrackEvent(Types.SDK, name, params);
            }
        });
    }

    public static synchronized void trackSdkEvent(final String name) {
        trackSdkEvent(name, new HashMap<String, String>());
    }

    public static synchronized void trackError(final String code,
                                                  final String message,
                                                  final Map<String, String> params) {
        if(instance == null) {
            return;
        }

        ThreadPoolInteractorExecuter.getInstance().executeInBackground(new Runnable(){
            @Override
            public void run() {
                getInstance().performTrackError(code, message, params);
            }
        });
    }

    public static synchronized void trackError(final String code,
                                               final String message) {
        trackError(code, message, new HashMap<String, String>());
    }

    private final AppEventSink sink;

    private AppEventClient(final AppEventSink sink) {
        this.sink = sink;
    }

    private void performTrackEvent(final String type,
                                   final String name,
                                   final Map<String, String> params) {
        sink.publishEvent(type, name, params);
    }

    private void performTrackError(final String code,
                                   final String message,
                                   final Map<String, String> params) {
        sink.publishError(code, message, params);
    }
}
