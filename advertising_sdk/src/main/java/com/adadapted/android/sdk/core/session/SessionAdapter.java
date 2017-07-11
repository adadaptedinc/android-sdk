package com.adadapted.android.sdk.core.session;

import com.adadapted.android.sdk.core.device.DeviceInfo;
import com.adadapted.android.sdk.core.zone.Zone;

import java.util.Map;

public interface SessionAdapter {
    interface SessionInitListener {
        void onSessionInitialized(Session session);
        void onSessionInitializeFailed();
    }

    interface AdGetListener {
        void onNewAdsLoaded(Map<String, Zone> zones);
        void onNewAdsLoadFailed();
    }

    interface Listener extends SessionInitListener, AdGetListener {}

    void sendInit(DeviceInfo deviceInfo, SessionInitListener listener);

    void sentAdGet(Session session, AdGetListener listener);
}
