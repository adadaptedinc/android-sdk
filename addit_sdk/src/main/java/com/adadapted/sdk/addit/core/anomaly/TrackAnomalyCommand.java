package com.adadapted.sdk.addit.core.anomaly;

import com.adadapted.sdk.addit.core.common.Command;

/**
 * Created by chrisweeden on 9/26/16.
 */

public class TrackAnomalyCommand extends Command {
    private final String eventPath;
    private final String code;
    private final String message;

    public TrackAnomalyCommand(final String eventPath,
                               final String code,
                               final String message) {
        this.eventPath = eventPath;
        this.code = code;
        this.message = message;
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
