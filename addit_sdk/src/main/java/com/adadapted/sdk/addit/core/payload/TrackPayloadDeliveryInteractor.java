package com.adadapted.sdk.addit.core.payload;

import com.adadapted.sdk.addit.core.common.Interactor;

/**
 * Created by chrisweeden on 2/9/17.
 */
public class TrackPayloadDeliveryInteractor implements Interactor {
    private final TrackPayloadDeliveryCommand command;
    private final PayloadEventTracker tracker;

    public TrackPayloadDeliveryInteractor(final TrackPayloadDeliveryCommand command,
                                          final PayloadEventTracker tracker) {
        this.command = command;
        this.tracker = tracker;
    }

    @Override
    public void execute() {
        if(command == null || tracker == null) {
            return;
        }

        tracker.trackEvent(command.getPayloadId(), command.getResult());
    }
}
