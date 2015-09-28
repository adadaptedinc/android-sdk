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
    private Map<String, String> mItems = new HashMap<>();

    // Here the key is the Replacement Text
    private Map<String, String> mReplacements = new HashMap<>();

    private Session mSession;

    AaSuggestionTracker(KeywordInterceptManager manager) {
        mManager = manager;
    }

    void suggestionMatched(Session session, String term, String replacement, String userInput) {
        mSession = session;

        term = term.toLowerCase();
        userInput = userInput.toLowerCase();
        replacement = replacement.toLowerCase();

        mItems.put(term, userInput);
        mReplacements.put(replacement, term);

        mManager.trackMatched(session, term, userInput);
    }

    public void suggestionPresented(String term) {
        term = term.toLowerCase();

        if(mItems.containsKey(term)) {
            mManager.trackPresented(mSession, term, mItems.get(term));
        }
    }

    public boolean suggestionSelected(String replacement) {
        replacement = replacement.toLowerCase();

        if(mReplacements.containsKey(replacement)) {
            String term = mReplacements.get(replacement);
            String userInput = mItems.get(term);

            mManager.trackSelected(mSession, term, userInput);

            return true;
        }

        return false;
    }
}
