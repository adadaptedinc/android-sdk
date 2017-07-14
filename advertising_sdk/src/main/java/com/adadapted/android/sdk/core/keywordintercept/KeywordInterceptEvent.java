package com.adadapted.android.sdk.core.keywordintercept;

import java.util.Date;

public class KeywordInterceptEvent {
    public static final String MATCHED  = "matched";
    public static final String PRESENTED  = "presented";
    public static final String SELECTED  = "selected";

    private final String appId;
    private final String sessionId;
    private final String udid;
    private final String searchId;
    private final Date datetime;
    private final String event;
    private final String userInput;
    private final String term;
    private final String sdkVersion;

    KeywordInterceptEvent(final String appId,
                          final String sessionId,
                          final String udid,
                          final String searchId,
                          final String event,
                          final String userInput,
                          final String term,
                          final String sdkVersion) {
        this.appId = (appId == null) ? "" : appId;
        this.sessionId =  (sessionId == null) ? "" : sessionId;
        this.udid = (udid == null) ? "" : udid;
        this.searchId = (searchId == null) ? "" : searchId;
        this.datetime = new Date();
        this.event = (event == null) ? "" : event;
        this.userInput = (userInput == null) ? "" : userInput;
        this.term = (term == null) ? "" : term;
        this.sdkVersion = (sdkVersion == null) ? "" : sdkVersion;
    }

    public String getAppId() {
        return appId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getUdid() {
        return udid;
    }

    public String getSearchId() {
        return searchId;
    }

    public Date getDatetime() {
        return datetime;
    }

    public String getEvent() {
        return event;
    }

    public String getUserInput() {
        return userInput;
    }

    public String getTerm() {
        return term;
    }

    public String getSdkVersion() {
        return sdkVersion;
    }

    public boolean supercedes(final KeywordInterceptEvent e) {
        return e != null && sessionId.equals(e.getSessionId()) &&
                event.equals(e.getEvent()) &&
                term.equals(e.getTerm()) &&
                userInput.contains(e.getUserInput());
    }
}
