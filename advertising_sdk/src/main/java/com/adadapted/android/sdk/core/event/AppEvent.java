package com.adadapted.android.sdk.core.event;

import java.util.Date;
import java.util.Map;

public class AppEvent {
    private final String type;
    private final String name;
    private final Map<String, String> params;
    private final long datetime;

    AppEvent(final String type,
             final String name,
             final Map<String, String> params) {
        this.type = type;
        this.name = name;
        this.params = params;
        this.datetime = (new Date()).getTime();
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public long getDatetime() {
        return datetime;
    }
}
