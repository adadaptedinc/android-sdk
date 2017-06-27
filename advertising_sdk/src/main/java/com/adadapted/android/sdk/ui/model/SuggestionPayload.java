package com.adadapted.android.sdk.ui.model;

import com.adadapted.android.sdk.ui.adapter.AaSuggestionTracker;

import java.util.Set;

public class SuggestionPayload {
    private final AaSuggestionTracker mSuggestionTracker;
    private final Set<String> mSuggestions;

    public SuggestionPayload(final AaSuggestionTracker suggestionTracker,
                             final Set<String> suggestions) {
        mSuggestionTracker = suggestionTracker;
        mSuggestions = suggestions;
    }

    public Set<String> getSuggestions() {
        return mSuggestions;
    }

    public void presented(final String term) {
        if(mSuggestionTracker != null) {
            mSuggestionTracker.suggestionPresented(term);
        }
    }

    public void selected(final String term) {
        if(mSuggestionTracker != null) {
            mSuggestionTracker.suggestionSelected(term);
        }
    }
}
