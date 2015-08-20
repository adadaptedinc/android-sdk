package com.adadapted.android.sdk.core.session;

import com.adadapted.android.sdk.core.session.model.Session;

/**
 * Created by chrisweeden on 8/18/15.
 */
public interface SessionListener {
    void onSessionInitialized(Session session);
    void onSessionInitFailed();
    void onNewAdsAvailable(Session session);
}
