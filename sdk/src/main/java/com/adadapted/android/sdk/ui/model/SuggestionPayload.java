package com.adadapted.android.sdk.ui.model;

import com.adadapted.android.sdk.ui.adapter.AaSuggestionTracker;

import java.util.Set;

/**
 * Created by chrisweeden on 7/17/15.
 */
public class SuggestionPayload {
    private final AaSuggestionTracker suggestionTracker;
    private Set<String> suggestions;

    public SuggestionPayload(AaSuggestionTracker suggestionTracker, Set<String> suggestions) {
        this.suggestionTracker = suggestionTracker;
        this.suggestions = suggestions;
    }

    public Set<String> getSuggestions() {
        return suggestions;
    }

    public void presented(String term) {
        suggestionTracker.suggestionPresented(term);
    }

    public void selected(String term) {
        suggestionTracker.suggestionSelected(term);
    }
}
