package com.adadapted.android.sdk.core.session;

import com.adadapted.android.sdk.core.device.DeviceInfo;

public interface SessionAdapter {
    interface SessionInitListener {
        void onSessionInitialized(Session session);
        void onSessionInitializeFailed();
    }

    interface AdGetListener {
        void onNewAdsLoaded(Session session);
        void onNewAdsLoadFailed();
    }

    interface Listener extends SessionInitListener, AdGetListener {}

    void sendInit(DeviceInfo deviceInfo, SessionInitListener listener);

    void sendRefreshAds(Session session, AdGetListener listener);
}
