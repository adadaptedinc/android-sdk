package com.adadapted.android.sdk.core.addit.payload;

import com.adadapted.android.sdk.core.common.Interactor;

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
