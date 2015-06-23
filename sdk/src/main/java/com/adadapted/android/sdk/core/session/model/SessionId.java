package com.adadapted.android.sdk.core.session.model;

/**
 * Created by chrisweeden on 6/5/15.
 */
public class SessionId {
    private final String value;

    public SessionId(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}
