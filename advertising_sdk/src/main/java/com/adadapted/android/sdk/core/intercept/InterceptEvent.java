package com.adadapted.android.sdk.core.intercept;

import java.util.Date;

public class InterceptEvent {
    static final String MATCHED  = "matched";
    static final String NOT_MATCHED  = "not_matched";
    static final String PRESENTED  = "presented";
    static final String SELECTED  = "selected";

    private final String searchId;
    private final Date createdAt;
    private final String event;
    private final String userInput;
    private final String termId;
    private final String term;

    InterceptEvent(final String searchId,
                   final String event,
                   final String userInput,
                   final String termId,
                   final String term) {
        this.searchId = (searchId == null) ? "" : searchId;
        this.createdAt = new Date();
        this.event = (event == null) ? "" : event;
        this.userInput = (userInput == null) ? "" : userInput;
        this.termId = (termId == null) ? "" : termId;
        this.term = (term == null) ? "" : term;
    }

    public String getSearchId() {
        return searchId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public String getEvent() {
        return event;
    }

    public String getUserInput() {
        return userInput;
    }

    public String getTermId() {
        return termId;
    }

    public String getTerm() {
        return term;
    }

    boolean supersedes(final InterceptEvent e) {
        return e != null &&
            event.equals(e.getEvent()) &&
            termId.equals(e.getTermId()) &&
            userInput.contains(e.getUserInput());
    }

    @Override
    public String toString() {
        return "InterceptEvent{" +
            "searchId='" + searchId + '\'' +
            ", createdAt=" + createdAt +
            ", event='" + event + '\'' +
            ", userInput='" + userInput + '\'' +
            ", termId='" + termId + '\'' +
            ", term='" + term + '\'' +
            '}';
    }
}
