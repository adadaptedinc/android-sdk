package com.adadapted.android.sdk.core.event;


import com.adadapted.android.sdk.core.common.Interactor;

public class RegisterAppEventInteractor implements Interactor {
    private final RegisterAppEventCommand command;
    private final AppEventTracker tracker;

    public RegisterAppEventInteractor(final RegisterAppEventCommand command,
                                      final AppEventTracker tracker) {
        this.command = command;
        this.tracker = tracker;
    }

    @Override
    public void execute() {
        tracker.trackEvent(command.getEventSource(), command.getEventName(), command.getEventParams());
    }
}
