package com.adadapted.android.sdk.core.keywordintercept.model;

import java.util.Map;

public class KeywordIntercept {
    private final String searchId;
    private final long refreshTime;
    private final int minMatchLength;
    private final Map<String, AutoFill> autoFill;

    public KeywordIntercept(final String searchId,
                            final long refreshTime,
                            final int minMatchLength,
                            final Map<String, AutoFill> autoFill) {
        this.searchId = searchId;
        this.refreshTime = refreshTime;
        this.minMatchLength = minMatchLength;
        this.autoFill = autoFill;
    }

    public String getSearchId() {
        return searchId;
    }

    public long getRefreshTime() {
        return refreshTime;
    }

    public int getMinMatchLength() {
        return minMatchLength;
    }

    public Map<String, AutoFill> getAutoFill() {
        return autoFill;
    }
}
