package com.adadapted.android.sdk.core.event;

import java.util.Map;

public class RegisterAppEventCommand {
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
