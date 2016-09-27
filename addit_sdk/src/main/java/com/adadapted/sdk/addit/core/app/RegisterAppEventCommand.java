package com.adadapted.sdk.addit.core.app;

import com.adadapted.sdk.addit.core.common.Command;

import java.util.Map;

/**
 * Created by chrisweeden on 9/26/16.
 */

public class RegisterAppEventCommand extends Command {
    private final String eventSource;
    private final String eventName;
    private final Map<String, String> eventParams;

    public RegisterAppEventCommand(final String eventSource,
                                   final String eventName,
                                   final Map<String, String> eventParams) {
        this.eventSource = eventSource;
        this.eventName = eventName;
        this.eventParams = eventParams;
    }

    public String getEventSource() {
        return eventSource;
    }

    public String getEventName() {
        return eventName;
    }

    public Map<String, String> getEventParams() {
        return eventParams;
    }
}
