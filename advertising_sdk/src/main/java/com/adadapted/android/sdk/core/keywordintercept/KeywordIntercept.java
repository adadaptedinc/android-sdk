package com.adadapted.android.sdk.core.keywordintercept;

import java.util.ArrayList;
import java.util.List;

public class KeywordIntercept {
    private static final String SEARCH_ID = "empty";
    private static final long REFRESH_TIME = 300;
    private static final int MIN_MATCH_LENGTH = 3;

    private final String searchId;
    private final long refreshTime;
    private final int minMatchLength;
    private final List<AutoFill> autoFill;

    public static KeywordIntercept empty() {
        return new KeywordIntercept(
            SEARCH_ID,
            REFRESH_TIME,
            MIN_MATCH_LENGTH,
            new ArrayList<AutoFill>()
        );
    }

    public KeywordIntercept(final String searchId,
                            final long refreshTime,
                            final int minMatchLength,
                            final List<AutoFill> autoFill) {
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

    public List<AutoFill> getAutoFill() {
        return autoFill;
    }
}
