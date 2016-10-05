package com.adadapted.android.sdk.core.ad;

import com.adadapted.android.sdk.core.ad.model.Ad;
import com.adadapted.android.sdk.core.session.model.Session;

/**
 * Created by chrisweeden on 9/29/16.
 */

public class RegisterAdEventCommand {
    private final Session session;
    private final Ad ad;
    private final String eventType;
    private final String eventName;

    public RegisterAdEventCommand(final Session session,
                                  final Ad ad,
                                  final String eventType,
                                  final String eventName) {
        this.session = session;
        this.ad = ad;
        this.eventType = eventType;
        this.eventName = eventName;
    }

    public Session getSession() {
        return session;
    }

    public Ad getAd() {
        return ad;
    }

    public String getEventType() {
        return eventType;
    }

    public String getEventName() {
        return eventName;
    }
}
