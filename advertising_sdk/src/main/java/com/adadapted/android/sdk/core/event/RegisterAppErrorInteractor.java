package com.adadapted.android.sdk.core.event;


import com.adadapted.android.sdk.core.common.Interactor;

/**
 * Created by chrisweeden on 9/29/16.
 */

public class RegisterAppErrorInteractor implements Interactor {
    private final RegisterAppErrorCommand command;
    private final AppErrorTracker tracker;

    public RegisterAppErrorInteractor(final RegisterAppErrorCommand command,
                                      final AppErrorTracker tracker) {
        this.command = command;
        this.tracker = tracker;
    }

    @Override
    public void execute() {
        tracker.trackError(
                command.getErrorCode(),
                command.getErrorMessage(),
                command.getErrorParams());
    }
}
