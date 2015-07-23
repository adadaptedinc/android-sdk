package com.adadapted.android.sdk.ui.adapter;

import com.adadapted.android.sdk.core.keywordintercept.KeywordInterceptManager;
import com.adadapted.android.sdk.core.session.model.Session;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by chrisweeden on 7/16/15.
 */
public class AaSuggestionTracker {
    private static final String TAG = AaSuggestionTracker.class.getName();

    private final KeywordInterceptManager manager;

    // Here the key is the Term Text
    private Map<String, String> items = new HashMap<>();

    // Here the key is the Replacement Text
    private Map<String, String> replacements = new HashMap<>();

    private Session session;

    AaSuggestionTracker(KeywordInterceptManager manager) {
        this.manager = manager;
    }

    void suggestionMatched(Session session, String term, String replacement, String userInput) {
        this.session = session;

        term = term.toLowerCase();
        userInput = userInput.toLowerCase();
        replacement = replacement.toLowerCase();

        items.put(term, userInput);
        replacements.put(replacement, term);

        manager.trackMatched(session, term, userInput);
    }

    public void suggestionPresented(String term) {
        term = term.toLowerCase();

        if(items.containsKey(term)) {
            manager.trackPresented(session, term, items.get(term));
        }
    }

    public boolean suggestionSelected(String replacement) {
        replacement = replacement.toLowerCase();

        if(replacements.containsKey(replacement)) {
            String term = replacements.get(replacement);
            String userInput = items.get(term);

            manager.trackSelected(session, term, userInput);

            return true;
        }

        return false;
    }
}
