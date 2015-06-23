package com.adadapted.android.sdk.core.zone.model;

/**
 * Created by chrisweeden on 6/5/15.
 */
public class ZoneId {
    private final String value;

    public ZoneId(final String value) {
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
