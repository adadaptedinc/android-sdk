package com.adadapted.android.sdk.ui.model;

import com.adadapted.android.sdk.ui.adapter.AaSuggestionTracker;

import java.util.Set;

/**
 * Created by chrisweeden on 7/17/15.
 */
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

    public void presented(String term) {
        if(mSuggestionTracker != null) {
            mSuggestionTracker.suggestionPresented(term);
        }
    }

    public void selected(String term) {
        if(mSuggestionTracker != null) {
            mSuggestionTracker.suggestionSelected(term);
        }
    }
}
