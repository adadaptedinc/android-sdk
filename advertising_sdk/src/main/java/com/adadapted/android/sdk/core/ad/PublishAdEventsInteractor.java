package com.adadapted.android.sdk.core.ad;

import com.adadapted.android.sdk.core.common.Interactor;

/**
 * Created by chrisweeden on 9/30/16.
 */

public class PublishAdEventsInteractor implements Interactor {
    private final AdEventTracker tracker;

    public PublishAdEventsInteractor(final AdEventTracker tracker) {
        this.tracker = tracker;
    }

    @Override
    public void execute() {
        tracker.publishEvents();
    }
}
