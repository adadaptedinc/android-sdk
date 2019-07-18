package com.adadapted.android.sdk.ui.adapter;

import com.adadapted.android.sdk.core.keywordintercept.KeywordInterceptClient;
import com.adadapted.android.sdk.core.session.Session;

import java.util.HashMap;
import java.util.Locale;
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
                                               final String termId,
                                               final String term,
                                               final String replacement,
                                               final String userInput) {
        getInstance().performSuggestionMatched(session, searchId, termId, term, replacement, userInput);
    }

    public static synchronized void suggestionPresented(final String searchId,
                                                        final String termId,
                                                        final String replacement) {
        getInstance().performSuggestionPresented(searchId, termId, replacement);
    }

    public static synchronized  void suggestionSelected(final String searchId,
                                                        final String termId,
                                                        final String replacement) {
        getInstance().performSuggestionSelected(searchId, termId, replacement);
    }

    static synchronized void suggestionNotMatched(final Session session,
                                                  final String searchId,
                                                  final String userInput) {
        getInstance().performSuggestionNotMatched(session, searchId, userInput);
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
                                          final String termId,
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

            KeywordInterceptClient.trackMatched(mSession, searchId, termId, lcTerm, lcUserInput);
        }
        finally {
            matcherLock.unlock();
        }
    }

    private void performSuggestionPresented(final String searchId,
                                            final String termId,
                                            final String replacement) {
        final String lcReplacement = convertToLowerCase(replacement);

        matcherLock.lock();
        try {
            if(replacements.containsKey(lcReplacement)) {
                final String term = replacements.get(lcReplacement);
                final String userInput = items.get(term);

                KeywordInterceptClient.trackPresented(mSession, searchId, termId,  term, userInput);
            }
        }
        finally {
            matcherLock.unlock();
        }
    }

    private void performSuggestionSelected(final String searchId,
                                           final String termId,
                                           final String replacement) {
        final String lcReplacement = convertToLowerCase(replacement);

        matcherLock.lock();
        try {
            if(replacements.containsKey(lcReplacement)) {
                final String term = replacements.get(lcReplacement);
                final String userInput = items.get(term);

                KeywordInterceptClient.trackSelected(mSession, searchId, termId, term, userInput);
            }
        }
            finally {
            matcherLock.unlock();
        }
    }

    private void performSuggestionNotMatched(final Session session,
                                             final String searchId,
                                             final String userInput) {
        matcherLock.lock();
        try {
            mSession = session;
            final String lcUserInput = convertToLowerCase(userInput);
            KeywordInterceptClient.trackNotMatched(mSession, searchId, lcUserInput);
        }
        finally {
            matcherLock.unlock();
        }
    }

    private String convertToLowerCase(final String str) {
        return str != null ? str.toLowerCase(Locale.ROOT) : "";
    }
}
