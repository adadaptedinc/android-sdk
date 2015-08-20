package com.adadapted.android.sdk.ui.model;

import com.adadapted.android.sdk.ui.adapter.AaSuggestionTracker;

import java.util.Set;

/**
 * Created by chrisweeden on 7/17/15.
 */
public class SuggestionPayload {
    private final AaSuggestionTracker mSuggestionTracker;
    private Set<String> mSuggestions;

    public SuggestionPayload(AaSuggestionTracker suggestionTracker, Set<String> suggestions) {
        mSuggestionTracker = suggestionTracker;
        mSuggestions = suggestions;
    }

    public Set<String> getSuggestions() {
        return mSuggestions;
    }

    public void presented(String term) {
        mSuggestionTracker.suggestionPresented(term);
    }

    public void selected(String term) {
        mSuggestionTracker.suggestionSelected(term);
    }
}
