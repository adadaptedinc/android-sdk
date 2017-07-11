package com.adadapted.android.sdk.core.session;

import android.content.Context;
import android.util.Log;

import com.adadapted.android.sdk.core.concurrency.ThreadPoolInteractorExecuter;
import com.adadapted.android.sdk.core.device.DeviceInfo;
import com.adadapted.android.sdk.core.device.DeviceInfoClient;
import com.adadapted.android.sdk.core.zone.Zone;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SessionClient implements SessionAdapter.Listener {
    private static final String LOGTAG = SessionClient.class.getName();

    public static void start(final Context context,
                             final String appId,
                             final boolean isProd,
                             final Map<String, String> params,
                             final Listener listener) {
        if(listener != null) {
            addListener(listener);
        }

        DeviceInfoClient.collectDeviceInfo(context, appId, isProd, params, new DeviceInfoClient.Callback() {
            @Override
            public void onDeviceInfoCollected(final DeviceInfo deviceInfo) {
                SessionClient.initialize(deviceInfo);
            }
        });
    }

    public static void restart(final Context context,
                               final String appId,
                               final boolean isProd,
                               final Map<String, String> params) {
        DeviceInfoClient.collectDeviceInfo(context, appId, isProd, params, new DeviceInfoClient.Callback() {
            @Override
            public void onDeviceInfoCollected(final DeviceInfo deviceInfo) {
                SessionClient.initialize(deviceInfo);
            }
        });
    }

    public interface Listener {
        void onSessionAvailable(Session session);
        void onAdsAvailable(Session session);
        void onSessionInitFailed();
    }

    private static SessionClient instance;

    public static SessionClient createInstance(final SessionAdapter adapter) {
        if(instance == null) {
            instance = new SessionClient(adapter);
        }

        return instance;
    }

    private static SessionClient getInstance() {
        return instance;
    }

    private static void initialize(final DeviceInfo deviceInfo) {
        if(instance == null) {
            return;
        }

        ThreadPoolInteractorExecuter.getInstance().executeInBackground(new Runnable(){
            @Override
            public void run() {
                getInstance().performInitialize(deviceInfo);
            }
        });
    }

    public static Session getCurrentSession() {
        if(instance == null) {
            return null;
        }

        return getInstance().currentSession;
    }

    public static void getSession(final Listener listener) {
        addListener(listener);
    }

    public static void addListener(Listener listener) {
        if(instance == null) {
            return;
        }

        getInstance().performAddListener(listener);
    }

    public static void removeListener(Listener listener) {
        if(instance == null) {
            return;
        }

        getInstance().performRemoveListener(listener);
    }

    private final SessionAdapter adapter;

    private final Set<Listener> listeners;
    private final Lock listenerLock = new ReentrantLock();

    private DeviceInfo deviceInfo;
    private final Lock deviceInfoLock = new ReentrantLock();

    private Session currentSession;
    private final Lock sessionLock = new ReentrantLock();

    private boolean pollingTimerRunning = false;

    private SessionClient(final SessionAdapter adapter) {
        this.adapter = adapter;
        this.listeners = new HashSet<>();
    }

    private void performAddListener(final Listener listener) {
        if(listener == null) {
            return;
        }

        listenerLock.lock();
        try {
            listeners.add(listener);
        }
        finally {
            listenerLock.unlock();
        }

        if(currentSession != null) {
            listener.onSessionAvailable(currentSession);
        }
    }

    private void performRemoveListener(final Listener listener) {
        if(listener == null) {
            return;
        }

        listenerLock.lock();
        try {
            listeners.remove(listener);
        }
        finally {
            listenerLock.unlock();
        }
    }

    private void performInitialize(final DeviceInfo deviceInfo) {
        deviceInfoLock.lock();
        try {
            this.deviceInfo = deviceInfo;
            adapter.sendInit(deviceInfo, this);
        }
        finally {
            deviceInfoLock.unlock();
        }
    }

    private void perfrmRefreshAds() {
        adapter.sentAdGet(currentSession, this);
    }

    private void updateCurrentSession(final Session session) {
        sessionLock.lock();
        try {
            currentSession = session;
            startPollingTimer();
        }
        finally {
            sessionLock.unlock();
        }
    }

    private void updateCurrentZones(final Map<String, Zone> zones) {
        sessionLock.lock();
        try {
            currentSession = new Session(currentSession, zones);
            startPollingTimer();
        }
        finally {
            sessionLock.unlock();
        }
    }

    private void startPollingTimer() {
        if(pollingTimerRunning) {
            return;
        }

        pollingTimerRunning = true;
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                pollingTimerRunning = false;
                if(currentSession.hasExpired()) {
                    Log.i(LOGTAG, "Session has expired. Expired at: " + currentSession.getExpiresAt());
                    performInitialize(deviceInfo);
                } else {
                    perfrmRefreshAds();
                }
            }
        }, currentSession.getRefreshTime());
    }

    private void notifySessionAvailable() {
        listenerLock.lock();
        try {
            for(final Listener l : listeners) {
                l.onSessionAvailable(currentSession);
            }
        }
        finally {
            listenerLock.unlock();
        }
    }

    private void notifyAdsAvailable() {
        listenerLock.lock();
        try {
            for(final Listener l : listeners) {
                l.onAdsAvailable(currentSession);
            }
        }
        finally {
            listenerLock.unlock();
        }
    }

    private void notifySessionInitFailed() {
        listenerLock.lock();
        try {
            for(final Listener l : listeners) {
                l.onSessionInitFailed();
            }
        }
        finally {
            listenerLock.unlock();
        }
    }

    @Override
    public void onSessionInitialized(final Session session) {
        updateCurrentSession(session);
        notifySessionAvailable();
    }

    @Override
    public void onSessionInitializeFailed() {}

    @Override
    public void onNewAdsLoaded(final Map<String, Zone> zones) {
        updateCurrentZones(zones);
        notifyAdsAvailable();
    }

    @Override
    public void onNewAdsLoadFailed() {}
}
