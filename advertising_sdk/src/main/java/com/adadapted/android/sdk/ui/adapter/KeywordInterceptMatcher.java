package com.adadapted.android.sdk.ui.adapter;

import com.adadapted.android.sdk.config.EventStrings;
import com.adadapted.android.sdk.core.event.AppEventClient;
import com.adadapted.android.sdk.core.intercept.InterceptClient;
import com.adadapted.android.sdk.core.session.Session;
import com.adadapted.android.sdk.core.session.SessionClient;
import com.adadapted.android.sdk.core.intercept.Term;
import com.adadapted.android.sdk.core.intercept.Intercept;
import com.adadapted.android.sdk.core.session.SessionListener;
import com.adadapted.android.sdk.ui.model.Suggestion;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class KeywordInterceptMatcher extends SessionListener implements InterceptClient.Listener {
    @SuppressWarnings("unused")
    private static final String LOGTAG = KeywordInterceptMatcher.class.getName();

    private final Lock interceptLock = new ReentrantLock();
    private Intercept intercept;
    private boolean mLoaded = false;

    private static class Contains {
        boolean found;
        String input;
        Term term;

        Contains() {
            found = false;
            input = "";
            term = null;
        }
    }

    public KeywordInterceptMatcher() {
        intercept = new Intercept();
        SessionClient.Companion.getInstance().getSession(this);
    }

    public Set<Suggestion> match(final CharSequence constraint) {
        final Set<Suggestion> suggestions = new HashSet<>();

        interceptLock.lock();
        try {
            final String input = constraint.toString().toLowerCase(Locale.ROOT);
            if(!shouldCheckConstraint(input)) {
                return suggestions;
            }

            final Contains contains = new Contains();
            for (final Term term : intercept.getTerms()) {
                if (term == null) {
                    continue;
                }

                final String lcTerm = term.getTerm().toLowerCase(Locale.ROOT);
                if (lcTerm.startsWith(input)) {
                    fileTerm(term, constraint.toString(), suggestions);
                    break;
                }
                else if (!contains.found && lcTerm.contains(input)) {
                    contains.found = true;
                    contains.input = input;
                    contains.term = term;
                }
            }

            if(suggestions.isEmpty()) {
                if(contains.found) {
                    fileTerm(contains.term, contains.input, suggestions);
                } else {
                    SuggestionTracker.suggestionNotMatched(
                        intercept.getSearchId(),
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

    private void fileTerm(final Term term, final String input, final Set<Suggestion> suggestions) {
        if(term != null) {
            suggestions.add(new Suggestion(intercept.getSearchId(), term));

            SuggestionTracker.suggestionMatched(
                    intercept.getSearchId(),
                    term.getTermId(),
                    term.getTerm(),
                    term.getReplacement(),
                    input
            );
        }
    }

    private boolean shouldCheckConstraint(final String input) {
        return isLoaded() &&
                input != null &&
                input.length() >= intercept.getMinMatchLength();
    }

    private boolean isLoaded() {
        interceptLock.lock();
        try {
            return mLoaded && intercept != null;
        }
        finally {
            interceptLock.unlock();
        }
    }

    @Override
    public void onKeywordInterceptInitialized(final Intercept intercept) {
        AppEventClient.Companion.getInstance().trackSdkEvent(EventStrings.KI_INITIALIZED);

        interceptLock.lock();
        try {
            this.intercept = intercept;
            mLoaded = true;
        }
        finally {
            interceptLock.unlock();
        }
    }

    @Override
    public void onSessionAvailable(final Session session) {
        if(!session.getId().isEmpty()) {
            InterceptClient.Companion.getInstance().initialize(session, this);
        }
    }

    @Override
    public void onAdsAvailable(final Session session) {
        if(!session.getId().isEmpty()) {
            InterceptClient.Companion.getInstance().initialize(session, this);
        }
    }
}
