package com.adadapted.android.sdk.core.anomaly;

import com.adadapted.android.sdk.core.common.Interactor;

/**
 * Created by chrisweeden on 9/29/16.
 */

public class RegisterAdAnomalyInteractor implements Interactor {
    private final RegisterAdAnomalyCommand command;
    private final AdAnomalyTracker tracker;

    public RegisterAdAnomalyInteractor(final RegisterAdAnomalyCommand command,
                                       final AdAnomalyTracker tracker) {
        this.command = command;
        this.tracker = tracker;
    }

    @Override
    public void execute() {
        tracker.registerAnomaly(
                command.getmSession(),
                command.getAdId(),
                command.getEventPath(),
                command.getCode(),
                command.getMessage());

        tracker.publishAnomalies();
    }
}