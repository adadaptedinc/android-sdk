package com.adadapted.android.sdk.core.keywordintercept.model;

import java.util.Map;

/**
 * Created by chrisweeden on 6/23/15.
 */
public class KeywordIntercept {
    private final String searchId;
    private final long refreshTime;
    private final Map<String, AutoFill> autofill;

    public KeywordIntercept(String searchId, long refreshTime, Map<String, AutoFill> autofill) {
        this.searchId = searchId;
        this.refreshTime = refreshTime;
        this.autofill = autofill;
    }

    public String getSearchId() {
        return searchId;
    }

    public long getRefreshTime() {
        return refreshTime;
    }

    public Map<String, AutoFill> getAutofill() {
        return autofill;
    }
}
