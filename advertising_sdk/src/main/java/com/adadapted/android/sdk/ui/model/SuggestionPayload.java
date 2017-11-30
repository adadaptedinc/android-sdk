package com.adadapted.android.sdk.ui.model;

import com.adadapted.android.sdk.ui.adapter.AaSuggestionTracker;

import java.util.Set;

public class SuggestionPayload {
    private final String searchId;
    private final AaSuggestionTracker suggestionTracker;
    private final Set<String> suggestions;

    public SuggestionPayload(final String searchId,
                             final AaSuggestionTracker suggestionTracker,
                             final Set<String> suggestions) {
        this.searchId = searchId;
        this.suggestionTracker = suggestionTracker;
        this.suggestions = suggestions;
    }

    public Set<String> getSuggestions() {
        return suggestions;
    }

    public void presented(final String term) {
        if(suggestionTracker != null) {
            suggestionTracker.suggestionPresented(searchId, term);
        }
    }

    public void selected(final String term) {
        if(suggestionTracker != null) {
            suggestionTracker.suggestionSelected(searchId, term);
        }
    }
}
