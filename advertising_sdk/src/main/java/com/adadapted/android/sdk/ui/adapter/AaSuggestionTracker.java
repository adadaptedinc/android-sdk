package com.adadapted.android.sdk.ui.adapter;

import com.adadapted.android.sdk.core.keywordintercept.KeywordInterceptManager;
import com.adadapted.android.sdk.core.session.model.Session;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by chrisweeden on 7/16/15
 */
public class AaSuggestionTracker {
    private static final String LOGTAG = AaSuggestionTracker.class.getName();

    private final KeywordInterceptManager mManager;

    // Here the key is the Term Text
    private final Map<String, String> mItems = new HashMap<>();

    // Here the key is the Replacement Text
    private final Map<String, String> mReplacements = new HashMap<>();

    private Session mSession;

    AaSuggestionTracker(final KeywordInterceptManager manager) {
        mManager = manager;
    }

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

        mManager.trackMatched(session, lcTerm, lcUserInput);
    }

    public void suggestionPresented(String term) {
        final String lcTerm = convertToLowerCase(term);

        if(mItems.containsKey(lcTerm)) {
            mManager.trackPresented(mSession, lcTerm, mItems.get(lcTerm));
        }
    }

    public boolean suggestionSelected(String replacement) {
        final String lcReplacement = convertToLowerCase(replacement);

        if(mReplacements.containsKey(lcReplacement)) {
            final String term = mReplacements.get(lcReplacement);
            final String userInput = mItems.get(term);

            mManager.trackSelected(mSession, term, userInput);

            return true;
        }

        return false;
    }

    private String convertToLowerCase(final String str) {
        return str != null ? str.toLowerCase() : "";
    }
}
