package com.adadapted.android.sdk.core.intercept;

import java.util.ArrayList;
import java.util.List;

public class Intercept {
    private static final String SEARCH_ID = "empty";
    private static final long REFRESH_TIME = 300;
    private static final int MIN_MATCH_LENGTH = 3;

    private final String searchId;
    private final long refreshTime;
    private final int minMatchLength;
    private final List<Term> terms;

    public static Intercept empty() {
        return new Intercept(
            SEARCH_ID,
            REFRESH_TIME,
            MIN_MATCH_LENGTH,
            new ArrayList<Term>()
        );
    }

    public Intercept(final String searchId,
                     final long refreshTime,
                     final int minMatchLength,
                     final List<Term> terms) {
        this.searchId = searchId;
        this.refreshTime = refreshTime;
        this.minMatchLength = minMatchLength;
        this.terms = terms;
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

    public List<Term> getTerms() {
        return terms;
    }
}
