package com.adadapted.android.sdk.core.keywordintercept.model;

import java.util.Date;

/**
 * Created by chrisweeden on 6/29/15.
 */
public class KeywordInterceptEvent {
    public static final String PRESENTED  = "presented";
    public static final String SELECTED  = "selected";

    private final String appId;
    private final String sessionId;
    private final String udid;
    private final Date datetime;
    private final String event;
    private final String userInput;
    private final String term;
    private final String sdkVersion;

    public KeywordInterceptEvent(String appId, String sessionId, String udid, String event,
                                 String userInput, String term, String sdkVersion) {
        this.appId = appId;
        this.sessionId = sessionId;
        this.udid = udid;
        this.datetime = new Date();
        this.event = event;
        this.userInput = userInput;
        this.term = term;
        this.sdkVersion = sdkVersion;
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

    public boolean supercedes(KeywordInterceptEvent e) {
        return this.sessionId.equals(e.getSessionId()) &&
                this.term.equals(e.getTerm()) &&
                this.userInput.contains(e.getUserInput());
    }

    @Override
    public String toString() {
        return "KeywordInterceptEvent{" +
                ", event='" + event + '\'' +
                ", userInput='" + userInput + '\'' +
                ", term='" + term + '\'' +
                '}';
    }
}