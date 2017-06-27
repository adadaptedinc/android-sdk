package com.adadapted.android.sdk.core.anomaly;

import com.adadapted.android.sdk.core.session.model.Session;

public class RegisterAdAnomalyCommand {
    private final Session mSession;
    private final String adId;
    private final String eventPath;
    private final String code;
    private final String message;

    public RegisterAdAnomalyCommand(final Session mSession,
                                    final String adId,
                                    final String eventPath,
                                    final String code,
                                    final String message) {
        this.mSession = mSession;
        this.adId = adId;
        this.eventPath = eventPath;
        this.code = code;
        this.message = message;
    }

    public Session getSession() {
        return mSession;
    }

    public String getAdId() {
        return adId;
    }

    public String getEventPath() {
        return eventPath;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
