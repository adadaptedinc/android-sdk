package com.adadapted.sdk.addit.core.anomaly;

import com.adadapted.sdk.addit.core.common.Interactor;

/**
 * Created by chrisweeden on 9/26/16.
 */

public class TrackAnomalyInteractor implements Interactor {
    private final TrackAnomalyCommand command;
    private final AnomalyTracker tracker;

    public TrackAnomalyInteractor(final TrackAnomalyCommand command,
                                  final AnomalyTracker tracker) {
        this.command = command;
        this.tracker = tracker;
    }

    @Override
    public void execute() {
        tracker.trackAnomaly(
                command.getEventPath(),
                command.getCode(),
                command.getMessage());
    }
}
