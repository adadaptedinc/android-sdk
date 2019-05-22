package com.adadapted.android.sdk.ui.adapter;

import com.adadapted.android.sdk.core.event.AppEventClient;
import com.adadapted.android.sdk.core.keywordintercept.KeywordInterceptClient;
import com.adadapted.android.sdk.core.session.Session;
import com.adadapted.android.sdk.core.session.SessionClient;
import com.adadapted.android.sdk.core.keywordintercept.AutoFill;
import com.adadapted.android.sdk.core.keywordintercept.KeywordIntercept;
import com.adadapted.android.sdk.ui.model.Suggestion;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class KeywordInterceptMatcher implements SessionClient.Listener, KeywordInterceptClient.Listener {
    @SuppressWarnings("unused")
    private static final String LOGTAG = KeywordInterceptMatcher.class.getName();

    private final Lock interceptLock = new ReentrantLock();
    private KeywordIntercept keywordIntercept;
    private boolean mLoaded = false;
    private Session mSession;

    private static class Contains {
        boolean found;
        String input;
        AutoFill autoFill;

        Contains() {
            found = false;
            input = "";
            autoFill = null;
        }
    }

    public KeywordInterceptMatcher() {
        keywordIntercept = KeywordIntercept.empty();
        SessionClient.getSession(this);
    }

    public Set<Suggestion> match(final CharSequence constraint) {
        final Set<Suggestion> suggestions = new HashSet<>();

        interceptLock.lock();
        try {
            final String input = constraint.toString().toLowerCase();
            if(!shouldCheckConstraint(input)) {
                return suggestions;
            }

            final Contains contains = new Contains();
            for (final AutoFill autoFill : keywordIntercept.getAutoFill()) {
                if (autoFill == null) {
                    continue;
                }

                final String lcTerm = autoFill.getTerm().toLowerCase();
                if (lcTerm.startsWith(input)) {
                    fileTerm(autoFill, constraint.toString(), suggestions);
                    break;
                }
                else if (!contains.found && lcTerm.contains(input)) {
                    contains.found = true;
                    contains.input = input;
                    contains.autoFill = autoFill;
                }
            }

            if(suggestions.isEmpty()) {
                if(contains.found) {
                    fileTerm(contains.autoFill, contains.input, suggestions);
                } else {
                    SuggestionTracker.suggestionNotMatched(
                            mSession,
                            keywordIntercept.getSearchId(),
                            constraint.toString()
                    );
                }
            }
        }
        finally {
            interceptLock.unlock();
        }

        return suggestions;
    }

    private void fileTerm(final AutoFill autoFill, final String input, final Set<Suggestion> suggestions) {
        if(autoFill != null) {
            suggestions.add(new Suggestion(keywordIntercept.getSearchId(), autoFill));

            SuggestionTracker.suggestionMatched(
                    mSession,
                    keywordIntercept.getSearchId(),
                    autoFill.getTermId(),
                    autoFill.getTerm(),
                    autoFill.getReplacement(),
                    input
            );
        }
    }

    private boolean shouldCheckConstraint(final String input) {
        return isLoaded() &&
                input != null &&
                input.length() >= keywordIntercept.getMinMatchLength();
    }

    private boolean isLoaded() {
        interceptLock.lock();
        try {
            return mLoaded && keywordIntercept != null;
        }
        finally {
            interceptLock.unlock();
        }
    }

    @Override
    public void onKeywordInterceptInitialized(final KeywordIntercept keywordIntercept) {
        AppEventClient.trackAppEvent("ki_initialized");

        interceptLock.lock();
        try {
            this.keywordIntercept = keywordIntercept;
            mLoaded = true;
        }
        finally {
            interceptLock.unlock();
        }
    }

    @Override
    public void onSessionAvailable(final Session session) {
        interceptLock.lock();
        try {
            mSession = session;
        }
        finally {
            interceptLock.unlock();
        }

        KeywordInterceptClient.initialize(session, this);
    }

    @Override
    public void onAdsAvailable(final Session session) {
        interceptLock.lock();
        try {
            mSession = session;
        }
        finally {
            interceptLock.unlock();
        }

        KeywordInterceptClient.initialize(session, this);
    }

    @Override
    public void onSessionInitFailed() {}
}
