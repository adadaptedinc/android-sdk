package com.adadapted.android.sdk.core.event;

import java.util.Date;
import java.util.Map;

public class AppError {
    private final String code;
    private final String message;
    private final Map<String, String> params;
    private final long datetime;

    AppError(final String code,
             final String message,
             final Map<String, String> params) {
        this.code = code;
        this.message = message;
        this.params = params;
        this.datetime = (new Date()).getTime();
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public long getDatetime() {
        return datetime;
    }
}
