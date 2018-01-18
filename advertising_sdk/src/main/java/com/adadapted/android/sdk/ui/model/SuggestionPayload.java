package com.adadapted.android.sdk.ui.model;

import com.adadapted.android.sdk.core.keywordintercept.Suggestion;
import com.adadapted.android.sdk.ui.adapter.AaSuggestionTracker;

import java.util.Set;

public class SuggestionPayload {
    private final String searchId;
    private final AaSuggestionTracker suggestionTracker;
    private final Set<Suggestion> suggestions;

    public SuggestionPayload(final String searchId,
                             final AaSuggestionTracker suggestionTracker,
                             final Set<Suggestion> suggestions) {
        this.searchId = searchId;
        this.suggestionTracker = suggestionTracker;
        this.suggestions = suggestions;
    }

    public Set<Suggestion> getSuggestions() {
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
