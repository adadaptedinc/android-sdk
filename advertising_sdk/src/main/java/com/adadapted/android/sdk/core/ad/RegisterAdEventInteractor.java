package com.adadapted.android.sdk.core.ad;

import com.adadapted.android.sdk.core.common.Interactor;

public class RegisterAdEventInteractor implements Interactor {
    private final RegisterAdEventCommand command;
    private final AdEventTracker tracker;

    public RegisterAdEventInteractor(final RegisterAdEventCommand command,
                                     final AdEventTracker tracker) {
        this.command = command;
        this.tracker = tracker;
    }

    @Override
    public void execute() {
        tracker.trackEvent(
            command.getSession(),
            command.getAd(),
            command.getEventType(),
            command.getEventName()
        );
    }
}
