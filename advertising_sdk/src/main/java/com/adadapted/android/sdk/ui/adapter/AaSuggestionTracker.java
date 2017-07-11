package com.adadapted.android.sdk.ui.adapter;

import com.adadapted.android.sdk.core.keywordintercept.KeywordInterceptClient;
import com.adadapted.android.sdk.core.session.Session;

import java.util.HashMap;
import java.util.Map;

public class AaSuggestionTracker {
    @SuppressWarnings("unused")
    private static final String LOGTAG = AaSuggestionTracker.class.getName();

    // Here the key is the Term Text
    private final Map<String, String> mItems = new HashMap<>();

    // Here the key is the Replacement Text
    private final Map<String, String> mReplacements = new HashMap<>();

    private Session mSession;

    AaSuggestionTracker() {}

    void suggestionMatched(final Session session,
                           final String term,
                           final String replacement,
                           final String userInput) {
        mSession = session;

        final String lcTerm =  convertToLowerCase(term);
        final String lcUserInput = convertToLowerCase(userInput);
        final String lcReplacement = convertToLowerCase(replacement);

        mItems.put(lcTerm, lcUserInput);
        mReplacements.put(lcReplacement, lcTerm);

        KeywordInterceptClient.trackMatched(mSession, lcTerm, lcUserInput);
    }

    public void suggestionPresented(final String term) {
        final String lcTerm = convertToLowerCase(term);

        if(mItems.containsKey(lcTerm)) {
            KeywordInterceptClient.trackPresented(mSession, lcTerm, mItems.get(lcTerm));
        }
    }

    public boolean suggestionSelected(final String replacement) {
        final String lcReplacement = convertToLowerCase(replacement);

        if(mReplacements.containsKey(lcReplacement)) {
            final String term = mReplacements.get(lcReplacement);
            final String userInput = mItems.get(term);

            KeywordInterceptClient.trackSelected(mSession, term, userInput);

            return true;
        }

        return false;
    }

    private String convertToLowerCase(final String str) {
        return str != null ? str.toLowerCase() : "";
    }
}
