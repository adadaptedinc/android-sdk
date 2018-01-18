package com.adadapted.android.sdk.ui.adapter;

import com.adadapted.android.sdk.core.keywordintercept.KeywordInterceptClient;
import com.adadapted.android.sdk.core.session.Session;
import com.adadapted.android.sdk.ui.model.Suggestion;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SuggestionTracker {
    @SuppressWarnings("unused")
    private static final String LOGTAG = SuggestionTracker.class.getName();

    private static SuggestionTracker instance;

    public static SuggestionTracker getInstance() {
        if(instance == null) {
            instance = new SuggestionTracker();
        }

        return instance;
    }

    static synchronized void suggestionMatched(final Session session,
                                               final String searchId,
                                               final String term,
                                               final String replacement,
                                               final String userInput) {
        getInstance().performSuggestionMatched(session, searchId, term, replacement, userInput);
    }

    public static synchronized void suggestionPresented(final String searchId,
                                                        final String replacement) {
        getInstance().performSuggestionPresented(searchId, replacement);
    }

    public static synchronized  void suggestionSelected(final String searchId,
                                                        final String replacement) {
        getInstance().performSuggestionSelected(searchId, replacement);
    }

    private final Lock matcherLock = new ReentrantLock();

    // Here the key is the Term Text
    private final Map<String, String> items = new HashMap<>();

    // Here the key is the Replacement Text
    private final Map<String, String> replacements = new HashMap<>();

    private Session mSession;

    private SuggestionTracker() {}

    private void performSuggestionMatched(final Session session,
                                          final String searchId,
                                          final String term,
                                          final String replacement,
                                          final String userInput) {
        matcherLock.lock();
        try {
            mSession = session;

            final String lcTerm = convertToLowerCase(term);
            final String lcUserInput = convertToLowerCase(userInput);
            final String lcReplacement = convertToLowerCase(replacement);

            items.put(lcTerm, lcUserInput);
            replacements.put(lcReplacement, lcTerm);

            KeywordInterceptClient.trackMatched(mSession, searchId, lcTerm, lcUserInput);
        }
        finally {
            matcherLock.unlock();
        }
    }

    private void performSuggestionPresented(final String searchId,
                                            final String replacement) {
        final String lcReplacement = convertToLowerCase(replacement);

        matcherLock.lock();
        try {
            if(replacements.containsKey(lcReplacement)) {
                final String term = replacements.get(lcReplacement);
                final String userInput = items.get(term);

                KeywordInterceptClient.trackPresented(mSession, searchId,  term, userInput);
            }
        }
        finally {
            matcherLock.unlock();
        }
    }

    private void performSuggestionSelected(final String searchId,
                                           final String replacement) {
        final String lcReplacement = convertToLowerCase(replacement);

        matcherLock.lock();
        try {
            if(replacements.containsKey(lcReplacement)) {
                final String term = replacements.get(lcReplacement);
                final String userInput = items.get(term);

                KeywordInterceptClient.trackSelected(mSession, searchId,  term, userInput);
            }
        }
            finally {
            matcherLock.unlock();
        }
    }

    private String convertToLowerCase(final String str) {
        return str != null ? str.toLowerCase() : "";
    }
}
