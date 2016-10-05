package com.adadapted.android.sdk.core.keywordintercept;

import com.adadapted.android.sdk.core.common.Command;
import com.adadapted.android.sdk.core.session.model.Session;

/**
 * Created by chrisweeden on 9/29/16.
 */
public class RegisterKeywordInterceptEventCommand extends Command {
    private final Session session;
    private final String searchId;
    private final String term;
    private final String userInput;
    private final String eventType;

    public RegisterKeywordInterceptEventCommand(final Session session,
                                                final String searchId,
                                                final String term,
                                                final String userInput,
                                                final String eventType) {
        this.session = session;
        this.searchId = searchId;
        this.term = term;
        this.userInput = userInput;
        this.eventType = eventType;
    }

    public Session getSession() {
        return session;
    }

    public String getSearchId() {
        return searchId;
    }

    public String getTerm() {
        return term;
    }

    public String getUserInput() {
        return userInput;
    }

    public String getEventType() {
        return eventType;
    }
}
