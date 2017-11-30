package com.adadapted.android.sdk.ui.adapter;

import com.adadapted.android.sdk.core.keywordintercept.KeywordInterceptClient;
import com.adadapted.android.sdk.core.session.Session;

import java.util.HashMap;
import java.util.Map;

public class AaSuggestionTracker {
    @SuppressWarnings("unused")
    private static final String LOGTAG = AaSuggestionTracker.class.getName();

    // Here the key is the Term Text
    private final Map<String, String> items = new HashMap<>();

    // Here the key is the Replacement Text
    private final Map<String, String> replacements = new HashMap<>();

    private Session mSession;

    AaSuggestionTracker() {}

    void suggestionMatched(final Session session,
                           final String searchId,
                           final String term,
                           final String replacement,
                           final String userInput) {
        mSession = session;

        final String lcTerm =  convertToLowerCase(term);
        final String lcUserInput = convertToLowerCase(userInput);
        final String lcReplacement = convertToLowerCase(replacement);

        items.put(lcTerm, lcUserInput);
        replacements.put(lcReplacement, lcTerm);

        KeywordInterceptClient.trackMatched(mSession, searchId, lcTerm, lcUserInput);
    }

    public void suggestionPresented(final String searchId, final String term) {
        final String lcTerm = convertToLowerCase(term);

        if(items.containsKey(lcTerm)) {
            KeywordInterceptClient.trackPresented(mSession, searchId,  lcTerm, items.get(lcTerm));
        }
    }

    public boolean suggestionSelected(final String searchId, final String replacement) {
        final String lcReplacement = convertToLowerCase(replacement);

        if(replacements.containsKey(lcReplacement)) {
            final String term = replacements.get(lcReplacement);
            final String userInput = items.get(term);

            KeywordInterceptClient.trackSelected(mSession, searchId,  term, userInput);

            return true;
        }

        return false;
    }

    private String convertToLowerCase(final String str) {
        return str != null ? str.toLowerCase() : "";
    }
}
