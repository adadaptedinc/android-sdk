package com.adadapted.android.sdk.core.keywordintercept;

import com.adadapted.android.sdk.core.common.Interactor;

public class PublishKeywordInterceptEventsInteractor implements Interactor {
    private final KeywordInterceptEventTracker tracker;

    public PublishKeywordInterceptEventsInteractor(final KeywordInterceptEventTracker tracker) {
        this.tracker = tracker;
    }

    @Override
    public void execute() {
        this.tracker.publishEvents();
    }
}
