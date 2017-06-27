package com.adadapted.android.sdk.core.keywordintercept;

import com.adadapted.android.sdk.core.common.Interactor;

public class RegisterKeywordInterceptEventInteractor implements Interactor {
    private final RegisterKeywordInterceptEventCommand command;
    private final KeywordInterceptEventTracker tracker;

    public RegisterKeywordInterceptEventInteractor(final RegisterKeywordInterceptEventCommand command,
                                                   final KeywordInterceptEventTracker tracker) {
        this.command = command;
        this.tracker = tracker;
    }

    @Override
    public void execute() {
        this.tracker.trackEvent(
            command.getSession(),
            command.getSearchId(),
            command.getTerm(),
            command.getUserInput(),
            command.getEventType()
        );
    }
}
