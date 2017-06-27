package com.adadapted.android.sdk.core.ad;

import com.adadapted.android.sdk.core.session.model.Session;

public class RefreshAdsCommand {
    private final Session session;

    public RefreshAdsCommand(final Session session) {
        this.session = session;
    }

    public Session getSession() {
        return session;
    }
}
