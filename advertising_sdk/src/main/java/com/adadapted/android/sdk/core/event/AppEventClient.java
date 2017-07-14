package com.adadapted.android.sdk.core.event;

import android.util.Log;

import com.adadapted.android.sdk.core.concurrency.ThreadPoolInteractorExecuter;
import com.adadapted.android.sdk.core.device.DeviceInfo;
import com.adadapted.android.sdk.core.device.DeviceInfoClient;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class AppEventClient {
    private static final String LOGTAG = AppEventClient.class.getName();

    private static class Types {
        static final String SDK = "sdk";
        static final String APP = "app";
    }

    private static AppEventClient instance;

    public static void createInstance(final AppEventSink sink) {
        if(instance == null) {
            instance = new AppEventClient(sink);
        }
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

    public static synchronized void publishEvents() {
        if(instance == null) {
            return;
        }

        ThreadPoolInteractorExecuter.getInstance().executeInBackground(new Runnable() {
            @Override
            public void run() {
                getInstance().performPublishEvents();
                getInstance().performPublishErrors();
            }
        });
    }

    private final AppEventSink sink;

    private final Set<AppEvent> events;
    private final Lock eventLock = new ReentrantLock();

    private final Set<AppError> errors;
    private final Lock errorLock = new ReentrantLock();

    private AppEventClient(final AppEventSink sink) {
        this.sink = sink;

        this.events = new HashSet<>();
        this.errors = new HashSet<>();

        DeviceInfoClient.getDeviceInfo(new DeviceInfoClient.Callback() {
            @Override
            public void onDeviceInfoCollected(final DeviceInfo deviceInfo) {
                sink.generateWrappers(deviceInfo);
            }
        });
    }

    private void performTrackEvent(final String type,
                                   final String name,
                                   final Map<String, String> params) {
        //Log.i(LOGTAG, "App Event: " + type + " - " + name);
        eventLock.lock();
        try {
            events.add(new AppEvent(type, name, params));
        }
        finally {
            eventLock.unlock();
        }
    }

    private void performTrackError(final String code,
                                   final String message,
                                   final Map<String, String> params) {

        Log.w(LOGTAG, "App Error: " + code + " - " + message);
        errorLock.lock();
        try {
            errors.add(new AppError(code, message, params));
        }
        finally {
            errorLock.unlock();
        }
    }

    private void performPublishEvents() {
        eventLock.lock();
        try {
            if(!events.isEmpty()) {
                final Set<AppEvent> currentEvents = new HashSet<>(events);
                events.clear();
                sink.publishEvent(currentEvents);
            }
        } finally{
            eventLock.unlock();
        }
    }

    private void performPublishErrors() {
        errorLock.lock();
        try {
            if(!errors.isEmpty()) {
                final Set<AppError> currentErrors = new HashSet<>(errors);
                errors.clear();

                sink.publishError(currentErrors);
            }
        }
        finally {
            errorLock.unlock();
        }
    }
}
