package com.adadapted.android.sdk.core.session;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.adadapted.android.sdk.config.Config;
import com.adadapted.android.sdk.core.ad.AdEventClient;
import com.adadapted.android.sdk.core.concurrency.ThreadPoolInteractorExecuter;
import com.adadapted.android.sdk.core.device.DeviceInfo;
import com.adadapted.android.sdk.core.device.DeviceInfoClient;
import com.adadapted.android.sdk.core.event.AppEventClient;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SessionClient implements SessionAdapter.Listener {
    private static final String LOGTAG = SessionClient.class.getName();

    public interface Listener {
        void onSessionAvailable(Session session);
        void onAdsAvailable(Session session);
        void onSessionInitFailed();
    }

    private static SessionClient instance;

    public static void createInstance(final SessionAdapter adapter) {
        if(instance == null) {
            instance = new SessionClient(adapter);
        }
    }

    private static SessionClient getInstance() {
        return instance;
    }

    public static synchronized void start(final Context context,
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

    private static synchronized void initialize(final DeviceInfo deviceInfo) {
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

    public static synchronized Session getCurrentSession() {
        if(instance == null) {
            return null;
        }

        return getInstance().currentSession;
    }

    public static synchronized void getSession(final Listener listener) {
        addListener(listener);
    }

    public static synchronized void addListener(Listener listener) {
        if(instance == null) {
            return;
        }

        getInstance().performAddListener(listener);
    }

    public static synchronized void removeListener(Listener listener) {
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

    private boolean pollingTimerRunning;
    private boolean eventTimerRunning;

    private SessionClient(final SessionAdapter adapter) {
        this.adapter = adapter;
        this.listeners = new HashSet<>();

        this.pollingTimerRunning = false;
        this.eventTimerRunning = false;
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
        }
        finally {
            deviceInfoLock.unlock();
        }

        adapter.sendInit(deviceInfo, this);
    }

    private void performRefreshAds() {
        adapter.sentAdGet(currentSession, this);
    }

    private void updateCurrentSession(final Session session) {
        sessionLock.lock();
        try {
            currentSession = session;
            startPublishTimer();
        }
        finally {
            sessionLock.unlock();
        }

        startPollingTimer();
    }

    private void updateCurrentZones(final Session session) {
        sessionLock.lock();
        try {
            currentSession = session;
        }
        finally {
            sessionLock.unlock();
        }

        startPollingTimer();
    }

    private void startPollingTimer() {
        if(pollingTimerRunning || currentSession.getRefreshTime() == 0L) {
            return;
        }

        pollingTimerRunning = true;

        sessionLock.lock();
        try {
            Log.i(LOGTAG, "Starting Ad polling timer.");
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    pollingTimerRunning = false;
                    if (currentSession.hasExpired()) {
                        Log.i(LOGTAG, "Session has expired. Expired at: " + currentSession.getExpiresAt());
                        AppEventClient.trackSdkEvent("session_expired");

                        performInitialize(deviceInfo);
                    } else {
                        Log.i(LOGTAG, "Checking for more Ads.");
                        performRefreshAds();
                    }
                }
            }, currentSession.getRefreshTime());
        }
        finally {
            sessionLock.unlock();
        }
    }

    private void startPublishTimer() {
        if(eventTimerRunning) {
            return;
        }

        Log.i(LOGTAG, "Starting up the Event Publisher.");

        eventTimerRunning = true;

        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                AdEventClient.publishEvents();
                AppEventClient.publishEvents();

                handler.postDelayed(this, Config.DEFAULT_EVENT_POLLING);
            }
        };
        runnable.run();
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
    public void onSessionInitializeFailed() {
        updateCurrentSession(Session.emptySession(deviceInfo));
        notifySessionInitFailed();
    }

    @Override
    public void onNewAdsLoaded(final Session session) {
        updateCurrentZones(session);
        notifyAdsAvailable();
    }

    @Override
    public void onNewAdsLoadFailed() {
        updateCurrentZones(Session.emptySession(deviceInfo));
        notifyAdsAvailable();
    }
}
