package com.adadapted.android.sdk.core.keywordintercept.model;

import com.adadapted.android.sdk.core.ad.model.Ad;

import java.util.Map;

/**
 * Created by chrisweeden on 6/23/15.
 */
public class KeywordIntercept {
    private final String searchId;
    private final long refreshTime;
    private final Map<String, String> autofill;
    private final Map<String, Ad> trigger;

    public KeywordIntercept(String searchId, long refreshTime,
                            Map<String, String> autofill, Map<String, Ad> trigger) {
        this.searchId = searchId;
        this.refreshTime = refreshTime;
        this.autofill = autofill;
        this.trigger = trigger;
    }

    public String getSearchId() {
        return searchId;
    }

    public long getRefreshTime() {
        return refreshTime;
    }

    public Map<String, String> getAutofill() {
        return autofill;
    }

    public Map<String, Ad> getTrigger() {
        return trigger;
    }
}
