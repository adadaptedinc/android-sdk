package com.adadapted.android.sdk.core.ad;

import com.adadapted.android.sdk.core.common.Command;
import com.adadapted.android.sdk.core.session.model.Session;

public class RefreshAdsCommand extends Command {
    private final Session session;

    public RefreshAdsCommand(final Session session) {
        this.session = session;
    }

    public Session getSession() {
        return session;
    }
}
